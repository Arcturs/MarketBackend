CREATE TABLE IF NOT EXISTS refresh_token
(
    refresh_token VARCHAR(40) PRIMARY KEY,
    expire_date   TIMESTAMP NOT NULL,
    user_id       BIGINT    REFERENCES user_info (user_id) ON DELETE SET NULL
);