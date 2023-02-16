INSERT INTO product(name, price, amount)
VALUES ('Name 1', 100.00, 10),
       ('Name 2', 100.00, 12),
       ('Name 3', 56.60, 3);

INSERT INTO category(name)
VALUES ('N'),
       ('V');

INSERT INTO product_category(product_id, category_id)
VALUES (1, 1),
       (1, 2),
       (3, 1);

INSERT INTO user_info(name, surname, password_hash, email)
VALUES ('Admin', 'Admin', '$2a$10$1pCaZ.GgVDGNG9aMsoIE/eLFOxf5mCpUFTnbhhBW0S7VPfyYKWbUG', 'admin@com.com');

INSERT INTO user_role(user_id, role_id)
VALUES (1, 1),
       (1, 2);