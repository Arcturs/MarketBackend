CREATE TABLE IF NOT EXISTS product
(
    product_id  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name        VARCHAR(255)   NOT NULL UNIQUE,
    description TEXT,
    price       NUMERIC(11, 2) NOT NULL,
    amount      INTEGER        NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS product_name_index ON product (name);

CREATE TABLE IF NOT EXISTS category
(
    category_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS product_category
(
    product_category_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    category_id         BIGINT REFERENCES category (category_id) ON DELETE CASCADE NOT NULL,
    product_id          BIGINT REFERENCES product (product_id) ON DELETE CASCADE   NOT NULL
);

CREATE INDEX IF NOT EXISTS category_id_product_category_index ON product_category (category_id);

CREATE TABLE IF NOT EXISTS user_info
(
    user_id       BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    surname       VARCHAR(100) NOT NULL,
    password_hash VARCHAR(60)  NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE
);

CREATE UNIQUE INDEX IF NOT EXISTS email_user_info_index ON user_info (email);

CREATE TABLE IF NOT EXISTS role
(
    role_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name    VARCHAR(100) NOT NULL UNIQUE
);

INSERT INTO role(name)
VALUES ('ADMIN'),
       ('USER');

CREATE TABLE IF NOT EXISTS user_role
(
    user_role_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id      BIGINT REFERENCES user_info (user_id) ON DELETE CASCADE NOT NULL,
    role_id      BIGINT REFERENCES role (role_id) ON DELETE CASCADE      NOT NULL
);

CREATE TABLE IF NOT EXISTS refresh_token
(
    refresh_token VARCHAR(40) PRIMARY KEY,
    expire_date   TIMESTAMP NOT NULL,
    user_id       BIGINT    REFERENCES user_info (user_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS order_info
(
    order_number VARCHAR(40) PRIMARY KEY,
    is_paid      BOOLEAN   NOT NULL DEFAULT FALSE,
    created      TIMESTAMP NOT NULL,
    user_id      BIGINT    REFERENCES user_info (user_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS order_product
(
    order_product_id VARCHAR(40) PRIMARY KEY,
    order_number     VARCHAR(40) NOT NULL REFERENCES order_info (order_number) ON DELETE CASCADE,
    product_id       BIGINT REFERENCES product (product_id) ON DELETE CASCADE,
    amount           INTEGER     NOT NULL
);