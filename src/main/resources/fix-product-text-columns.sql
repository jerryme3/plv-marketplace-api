DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'products'
          AND column_name = 'title'
          AND data_type = 'bytea'
    ) THEN
        ALTER TABLE products
        ALTER COLUMN title TYPE VARCHAR(100)
        USING convert_from(title, 'UTF8');
    END IF;

    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'products'
          AND column_name = 'description'
          AND data_type = 'bytea'
    ) THEN
        ALTER TABLE products
        ALTER COLUMN description TYPE TEXT
        USING convert_from(description, 'UTF8');
    END IF;
END $$;
