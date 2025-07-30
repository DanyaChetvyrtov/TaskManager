CREATE TABLE local_profile
(
    id         uuid PRIMARY KEY,
    is_active  boolean DEFAULT true NOT NULL,
    deleted_at timestamp
);