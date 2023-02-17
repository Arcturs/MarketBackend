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
VALUES ('Admin', 'Admin', '$2a$10$1pCaZ.GgVDGNG9aMsoIE/eLFOxf5mCpUFTnbhhBW0S7VPfyYKWbUG', 'admin@com.com'),
       ('User', 'User', '$2a$10$1pCaZ.GgVDGNG9aMsoIE/eLFOxf5mCpUFTnbhhBW0S7VPfyYKWbUG', 'user@com.com');

INSERT INTO user_role(user_id, role_id)
VALUES (1, 1),
       (1, 2),
       (2, 2);

INSERT INTO order_info(order_number, is_paid, created, user_id)
VALUES ('9e4dca52-c91a-43d7-8abc-426af8730733', false, '2022-02-15 04:05:06', 2),
       ('57996957-a2cf-4435-a2d3-c6c4fb5d3a6c', true, '2022-02-14 18:30:21', 2),
       ('7da68357-5be1-4953-a4eb-90bfb9b6aebe', false, '2022-02-14 18:35:43', 1);

INSERT INTO order_product(order_product_id, order_number, product_id, amount)
VALUES ('2372ad93-c7b7-4f2d-9006-2dabd4e72703', '9e4dca52-c91a-43d7-8abc-426af8730733', 2, 3),
       ('c39f76b5-bc69-48bf-bd27-8a0dcb57ca30', '9e4dca52-c91a-43d7-8abc-426af8730733', 3, 1),
       ('afc26ea2-a0f4-4904-98a6-3c2b2caba282', '57996957-a2cf-4435-a2d3-c6c4fb5d3a6c', 2, 8),
       ('665119c8-63d9-4d7a-8454-15abef806042', '7da68357-5be1-4953-a4eb-90bfb9b6aebe', 3, 3);