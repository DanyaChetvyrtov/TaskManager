CREATE TABLE local_account
(
    id         uuid PRIMARY KEY,
    is_active  boolean DEFAULT true NOT NULL,
    deleted_at timestamp
);