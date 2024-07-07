DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT FROM pg_database WHERE datname = 'craft_demo'
        )
        THEN
            CREATE DATABASE craft_demo;
        END IF;
    END
$$;