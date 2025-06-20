CREATE EXTENSION IF NOT EXISTS pgcrypto; -- эт нужно для gen_random_uuid()

CREATE TABLE IF NOT EXISTS task
(
    id         uuid      DEFAULT gen_random_uuid() PRIMARY KEY,
    account_id uuid                    NOT NULL,
    title      varchar(100)            NOT NULL,
    body       text                    NOT NULL,
    completed  boolean   DEFAULT false NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp DEFAULT CURRENT_TIMESTAMP
);

