-- Run once against an existing PostgreSQL database before deploying this version.
ALTER TABLE orders
    ADD COLUMN IF NOT EXISTS seller_id INT REFERENCES sellers(seller_id) ON DELETE RESTRICT,
    ADD COLUMN IF NOT EXISTS delivery_location VARCHAR(100),
    ADD COLUMN IF NOT EXISTS current_location VARCHAR(100);

ALTER TABLE order_items
    ADD COLUMN IF NOT EXISTS product_title VARCHAR(100);

UPDATE order_items oi
SET product_title = p.title
FROM products p
WHERE oi.product_id = p.product_id
  AND oi.product_title IS NULL;

WITH single_seller_orders AS (
    SELECT oi.order_id, MIN(s.seller_id) AS seller_id
    FROM order_items oi
    JOIN products p ON p.product_id = oi.product_id
    JOIN stores st ON st.store_id = p.store_id
    JOIN sellers s ON s.seller_id = st.seller_id
    GROUP BY oi.order_id
    HAVING COUNT(DISTINCT s.seller_id) = 1
)
UPDATE orders o
SET seller_id = sso.seller_id
FROM single_seller_orders sso
WHERE o.order_id = sso.order_id
  AND o.seller_id IS NULL;

DO $$
DECLARE
    status_check RECORD;
BEGIN
    FOR status_check IN
        SELECT conname
        FROM pg_constraint
        WHERE conrelid = 'orders'::regclass
          AND contype = 'c'
          AND pg_get_constraintdef(oid) ILIKE '%status%'
    LOOP
        EXECUTE format('ALTER TABLE orders DROP CONSTRAINT %I', status_check.conname);
    END LOOP;
END $$;

UPDATE orders
SET status = CASE status
    WHEN 'DELIVERING' THEN 'SHIPPED'
    WHEN 'DELIVERED' THEN 'COMPLETED'
    ELSE status
END
WHERE status IN ('DELIVERING', 'DELIVERED');

ALTER TABLE orders
    ADD CONSTRAINT orders_status_check
    CHECK (total_amount >= 0 AND status IN (
        'PENDING', 'PAID', 'CONFIRMED', 'PACKED', 'SHIPPED', 'COMPLETED', 'CANCELLED', 'REFUNDED'
    ));

ALTER TABLE order_items
    ALTER COLUMN product_title SET NOT NULL;
