INSERT INTO user_info(name, surname, password_hash, email)
VALUES ('John', 'Smith', '$2a$10$1pCaZ.GgVDGNG9aMsoIE/eLFOxf5mCpUFTnbhhBW0S7VPfyYKWbUG', 'em1@jar.com'),
       ('NoRole', 'User', '$2a$10$1pCaZ.GgVDGNG9aMsoIE/eLFOxf5mCpUFTnbhhBW0S7VPfyYKWbUG', 'norole@norole.com');

INSERT INTO role(name)
VALUES ('ADMIN'),
       ('USER');

INSERT INTO user_role(user_id, role_id)
VALUES (1, 2);

INSERT INTO refresh_token(refresh_token, expire_date, user_id)
VALUES ('8e58d2a2-8ba3-4afd-9d8b-d68a15a0dec4', MAKE_TIMESTAMP(3022, 1, 29, 23, 16, 8.25), 1),
       ('08d171a8-1895-4340-a88f-1536fd8315b0', MAKE_TIMESTAMP(2021, 1, 29, 23, 16, 8.25), 1);