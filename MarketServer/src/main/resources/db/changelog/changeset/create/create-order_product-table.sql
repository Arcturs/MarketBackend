CREATE TABLE IF NOT EXISTS order_product
(
    order_product_id VARCHAR(40) PRIMARY KEY,
    order_number     VARCHAR(40) NOT NULL REFERENCES order_info (order_number) ON DELETE CASCADE,
    product_id       BIGINT REFERENCES product (product_id) ON DELETE CASCADE,
    amount           INTEGER     NOT NULL
);