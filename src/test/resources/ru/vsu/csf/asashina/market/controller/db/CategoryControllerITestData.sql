INSERT INTO product(name, price, amount)
VALUES ('Name 1', 100.0, 10),
       ('Name 2', 56.6, 3);

INSERT INTO category(name)
VALUES ('N'),
       ('V');

INSERT INTO product_category(product_id, category_id)
VALUES (1, 1),
       (1, 2),
       (2, 1);