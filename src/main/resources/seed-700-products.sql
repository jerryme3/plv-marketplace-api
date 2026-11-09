BEGIN;

-- Deterministic marketplace seed data.
-- Safe to run on a database that already contains ordinary application data.

INSERT INTO accounts (account_id, email, hashed_password, role)
SELECT
    100000 + n,
    'seed-seller-' || n || '@marketplace.test',
    '$2a$12$seedPasswordHashNotValidForAuthentication000000000000000000000000000',
    'ROLE_USER'
FROM generate_series(1, 20) AS n
ON CONFLICT (account_id) DO NOTHING;

INSERT INTO users (
    user_id, account_id, username, is_active, created_at, updated_at, last_login
)
SELECT
    100000 + n,
    100000 + n,
    'seed_seller_' || n,
    TRUE,
    TIMESTAMP WITH TIME ZONE '2025-01-01 08:00:00+00' + (n || ' days')::interval,
    TIMESTAMP WITH TIME ZONE '2025-01-01 08:00:00+00' + (n || ' days')::interval,
    TIMESTAMP WITH TIME ZONE '2026-10-01 08:00:00+00' + (n || ' hours')::interval
FROM generate_series(1, 20) AS n
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO sellers (seller_id, user_id, rating, is_verified)
SELECT
    110000 + n,
    100000 + n,
    (3.5 + ((n % 15) / 10.0))::numeric(3, 2),
    n % 4 <> 0
FROM generate_series(1, 20) AS n
ON CONFLICT (seller_id) DO NOTHING;

INSERT INTO categories (category_id, name, description)
VALUES
    (120001, 'Seed Textbooks & Notes', 'Course books, lecture notes, and study guides.'),
    (120002, 'Seed Electronics', 'Phones, computers, calculators, and campus technology.'),
    (120003, 'Seed Clothing & Accessories', 'Campus apparel, bags, jewelry, and everyday accessories.'),
    (120004, 'Seed Handmade & Crafts', 'Handcrafted goods and student-made merchandise.'),
    (120005, 'Seed Dorm & Home', 'Dorm furnishings, decorations, and household essentials.'),
    (120006, 'Seed Sports & Recreation', 'Sports equipment, fitness gear, and outdoor items.'),
    (120007, 'Seed Beauty & Wellness', 'Personal care and wellness products.'),
    (120008, 'Seed Art & Supplies', 'Art materials, stationery, and creative supplies.'),
    (120009, 'Seed Food & Pantry', 'Packaged snacks, pantry staples, and beverages.'),
    (120010, 'Seed Music & Instruments', 'Musical instruments and audio equipment.')
ON CONFLICT (category_id) DO NOTHING;

INSERT INTO stores (store_id, seller_id, name, description, created_at)
SELECT
    130000 + n,
    110000 + n,
    'Campus Store ' || n,
    'Marketplace storefront number ' || n || ' with student-focused products.',
    TIMESTAMP WITH TIME ZONE '2025-02-01 09:00:00+00' + (n || ' days')::interval
FROM generate_series(1, 20) AS n
ON CONFLICT (store_id) DO NOTHING;

INSERT INTO accounts (account_id, email, hashed_password, role)
SELECT
    200000 + n,
    'seed-buyer-' || n || '@marketplace.test',
    '$2a$12$seedPasswordHashNotValidForAuthentication000000000000000000000000000',
    'ROLE_USER'
FROM generate_series(1, 40) AS n
ON CONFLICT (account_id) DO NOTHING;

INSERT INTO users (
    user_id, account_id, username, is_active, created_at, updated_at, last_login
)
SELECT
    200000 + n,
    200000 + n,
    'seed_buyer_' || n,
    TRUE,
    TIMESTAMP WITH TIME ZONE '2025-03-01 08:00:00+00' + (n || ' days')::interval,
    TIMESTAMP WITH TIME ZONE '2025-03-01 08:00:00+00' + (n || ' days')::interval,
    TIMESTAMP WITH TIME ZONE '2026-10-10 08:00:00+00' + (n || ' hours')::interval
FROM generate_series(1, 40) AS n
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO buyers (buyer_id, user_id, delivery_building)
SELECT
    210000 + n,
    200000 + n,
    (ARRAY[
        'North Dormitory',
        'South Residence Hall',
        'Engineering Building',
        'Science Center',
        'Library Annex',
        'Student Center'
    ])[1 + (n % 6)]
FROM generate_series(1, 40) AS n
ON CONFLICT (buyer_id) DO NOTHING;

WITH product_catalog AS (
    SELECT *
    FROM (
        VALUES
            ('Campus Textbook Volume', 'Course textbook for student study and review.'),
            ('Lecture Notebook', 'Durable notebook for lectures and homework.'),
            ('Graphing Calculator', 'Scientific calculator for mathematics and lab work.'),
            ('USB-C Charger', 'Fast USB-C wall charger for phones and tablets.'),
            ('Wireless Mouse', 'Compact wireless mouse for laptops and desktop computers.'),
            ('Campus Backpack', 'Lightweight backpack with padded laptop compartment.'),
            ('Student T-Shirt', 'Comfortable cotton campus T-shirt.'),
            ('Handmade Study Journal', 'Student-made journal with recycled paper.'),
            ('Desk Organizer', 'Compact organizer for stationery and desk accessories.'),
            ('LED Desk Lamp', 'Adjustable LED lamp with multiple brightness levels.'),
            ('Resistance Band Set', 'Portable exercise bands for home workouts.'),
            ('Reusable Water Bottle', 'Insulated bottle for school and travel use.'),
            ('Hydrating Face Care Kit', 'Personal care kit for daily use.'),
            ('Sketch Pad', 'Blank sketch pad for drawing and illustration practice.'),
            ('Guitar Starter Pack', 'Beginner guitar accessories and maintenance kit.'),
            ('Campus Snack Box', 'Assorted packaged snacks for busy study days.'),
            ('Campus Hoodie', 'Warm fleece hoodie with university-inspired styling.'),
            ('Bluetooth Earbuds', 'Portable wireless earbuds for classes and commuting.'),
            ('Portable Power Bank', 'Compact battery pack for charging devices on the go.'),
            ('Reusable Lunch Kit', 'Bento lunch kit for campus meals.')
    ) AS catalog(title, description)
),
numbered_products AS (
    SELECT
        n,
        300000 + n AS product_id,
        catalog.title || ' #' || (1 + ((n - 1) / 20)) AS title,
        catalog.description AS description,
        130000 + 1 + ((n - 1) % 20) AS store_id,
        120001 + ((n - 1) % 10) AS category_id
    FROM generate_series(1, 700) AS n
    JOIN product_catalog AS catalog
      ON catalog.title = (
          SELECT title
          FROM product_catalog
          OFFSET ((n - 1) % 20)
          LIMIT 1
      )
)
INSERT INTO products (
    product_id, store_id, category_id, title, description, price, created_at, updated_at
)
SELECT
    product_id,
    store_id,
    category_id,
    LEFT(title, 100),
    description,
    ((49 + ((n * 37) % 95000) / 100.0)::numeric(10, 2)),
    TIMESTAMP WITH TIME ZONE '2025-04-01 09:00:00+00' + (n || ' hours')::interval,
    TIMESTAMP WITH TIME ZONE '2025-04-01 09:00:00+00' + (n || ' hours')::interval
FROM numbered_products
ON CONFLICT (product_id) DO NOTHING;

INSERT INTO product_stocks (product_stock_id, product_id, total_stocks, created_at, updated_at)
SELECT
    400000 + n,
    300000 + n,
    5 + ((n * 17) % 196),
    TIMESTAMP WITH TIME ZONE '2025-04-01 09:00:00+00' + (n || ' hours')::interval,
    TIMESTAMP WITH TIME ZONE '2025-04-01 09:00:00+00' + (n || ' hours')::interval
FROM generate_series(1, 700) AS n
ON CONFLICT (product_stock_id) DO NOTHING;

INSERT INTO carts (cart_id, buyer_id, created_at, updated_at)
SELECT
    500000 + n,
    210000 + n,
    TIMESTAMP WITH TIME ZONE '2025-05-01 09:00:00+00' + (n || ' hours')::interval,
    TIMESTAMP WITH TIME ZONE '2025-05-01 09:00:00+00' + (n || ' hours')::interval
FROM generate_series(1, 40) AS n
ON CONFLICT (cart_id) DO NOTHING;

INSERT INTO cart_items (cart_item_id, cart_id, product_id, quantity, added_at)
SELECT
    510000 + ((cart_number - 1) * 3) + item_number,
    500000 + cart_number,
    300000 + (cart_number * 17 + item_number * 113) % 700 + 1,
    1 + ((cart_number + item_number) % 4),
    TIMESTAMP WITH TIME ZONE '2025-05-02 09:00:00+00'
        + ((cart_number * 3 + item_number) || ' hours')::interval
FROM generate_series(1, 40) AS cart_number
CROSS JOIN generate_series(1, 3) AS item_number
ON CONFLICT (cart_item_id) DO NOTHING;

INSERT INTO orders (order_id, buyer_id, seller_id, total_amount, status, created_at, updated_at)
SELECT
    600000 + ((buyer_number - 1) * 2) + order_number,
    210000 + buyer_number,
    s.seller_id,
    0.00,
    (ARRAY['COMPLETED', 'SHIPPED', 'PENDING', 'CANCELLED'])
        [1 + ((buyer_number + order_number) % 4)],
    TIMESTAMP WITH TIME ZONE '2025-06-01 10:00:00+00'
        + (((buyer_number - 1) * 2 + order_number) || ' hours')::interval,
    TIMESTAMP WITH TIME ZONE '2025-06-01 10:00:00+00'
        + (((buyer_number - 1) * 2 + order_number) || ' hours')::interval
FROM generate_series(1, 40) AS buyer_number
CROSS JOIN generate_series(1, 2) AS order_number
JOIN products p
    ON p.product_id = 300000 + ((((buyer_number - 1) * 2 + order_number) * 29) % 700) + 1
JOIN stores st ON st.store_id = p.store_id
JOIN sellers s ON s.seller_id = st.seller_id
ON CONFLICT (order_id) DO NOTHING;

INSERT INTO order_items (
    order_item_id, order_id, product_id, product_title, quantity, unit_price
)
SELECT
    610000 + ((order_index - 1) * 2) + item_number,
    600000 + order_index,
    product.product_id,
    product.title,
    1 + ((order_index + item_number) % 3),
    (25 + ((order_index * 19 + item_number * 13) % 9500) / 100.0)::numeric(10, 2)
FROM generate_series(1, 80) AS order_index
CROSS JOIN generate_series(1, 2) AS item_number
JOIN products AS product
    ON product.product_id = 300000 + ((order_index * 29 + (item_number - 1) * 20) % 700) + 1
ON CONFLICT (order_item_id) DO NOTHING;

UPDATE orders AS o
SET total_amount = totals.amount
FROM (
    SELECT oi.order_id, SUM(oi.quantity * oi.unit_price) AS amount
    FROM order_items AS oi
    GROUP BY oi.order_id
) AS totals
WHERE o.order_id = totals.order_id;

INSERT INTO payments (
    payment_id, order_id, payment_method, payment_status, amount, paid_at
)
SELECT
    620000 + order_index,
    600000 + order_index,
    (ARRAY['GCASH', 'PAYMAYA', 'CASH_ON_DELIVERY'])[1 + (order_index % 3)],
    CASE
        WHEN o.status = 'COMPLETED' THEN 'PAID'
        WHEN o.status = 'CANCELLED' THEN 'CANCELLED'
        ELSE 'PENDING'
    END,
    o.total_amount,
    CASE WHEN o.status = 'COMPLETED' THEN o.updated_at ELSE NULL END
FROM generate_series(1, 80) AS order_index
JOIN orders AS o ON o.order_id = 600000 + order_index
ON CONFLICT (payment_id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('accounts', 'account_id'), GREATEST(COALESCE((SELECT MAX(account_id) FROM accounts), 1), 1), TRUE);
SELECT setval(pg_get_serial_sequence('users', 'user_id'), GREATEST(COALESCE((SELECT MAX(user_id) FROM users), 1), 1), TRUE);
SELECT setval(pg_get_serial_sequence('sellers', 'seller_id'), GREATEST(COALESCE((SELECT MAX(seller_id) FROM sellers), 1), 1), TRUE);
SELECT setval(pg_get_serial_sequence('categories', 'category_id'), GREATEST(COALESCE((SELECT MAX(category_id) FROM categories), 1), 1), TRUE);
SELECT setval(pg_get_serial_sequence('stores', 'store_id'), GREATEST(COALESCE((SELECT MAX(store_id) FROM stores), 1), 1), TRUE);
SELECT setval(pg_get_serial_sequence('buyers', 'buyer_id'), GREATEST(COALESCE((SELECT MAX(buyer_id) FROM buyers), 1), 1), TRUE);
SELECT setval(pg_get_serial_sequence('products', 'product_id'), GREATEST(COALESCE((SELECT MAX(product_id) FROM products), 1), 1), TRUE);
SELECT setval(pg_get_serial_sequence('product_stocks', 'product_stock_id'), GREATEST(COALESCE((SELECT MAX(product_stock_id) FROM product_stocks), 1), 1), TRUE);
SELECT setval(pg_get_serial_sequence('carts', 'cart_id'), GREATEST(COALESCE((SELECT MAX(cart_id) FROM carts), 1), 1), TRUE);
SELECT setval(pg_get_serial_sequence('cart_items', 'cart_item_id'), GREATEST(COALESCE((SELECT MAX(cart_item_id) FROM cart_items), 1), 1), TRUE);
SELECT setval(pg_get_serial_sequence('orders', 'order_id'), GREATEST(COALESCE((SELECT MAX(order_id) FROM orders), 1), 1), TRUE);
SELECT setval(pg_get_serial_sequence('order_items', 'order_item_id'), GREATEST(COALESCE((SELECT MAX(order_item_id) FROM order_items), 1), 1), TRUE);
SELECT setval(pg_get_serial_sequence('payments', 'payment_id'), GREATEST(COALESCE((SELECT MAX(payment_id) FROM payments), 1), 1), TRUE);

COMMIT;
