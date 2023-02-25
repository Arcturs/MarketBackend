CREATE TABLE IF NOT EXISTS order_info
(
    order_number VARCHAR(40) PRIMARY KEY,
    is_paid      BOOLEAN   NOT NULL DEFAULT FALSE,
    created      TIMESTAMP NOT NULL,
    user_id      BIGINT    REFERENCES user_info (user_id) ON DELETE SET NULL
);