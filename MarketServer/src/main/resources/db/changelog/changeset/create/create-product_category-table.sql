CREATE TABLE IF NOT EXISTS product_category
(
    product_category_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    category_id         BIGINT REFERENCES category (category_id) ON DELETE CASCADE NOT NULL,
    product_id          BIGINT REFERENCES product (product_id) ON DELETE CASCADE   NOT NULL
);