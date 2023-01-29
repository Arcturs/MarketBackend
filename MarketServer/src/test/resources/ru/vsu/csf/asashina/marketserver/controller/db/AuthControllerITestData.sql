INSERT INTO user_info(name, surname, password_hash, email)
VALUES ('John', 'Smith', '$2a$10$1pCaZ.GgVDGNG9aMsoIE/eLFOxf5mCpUFTnbhhBW0S7VPfyYKWbUG', 'em1@jar.com'),
       ('NoRole', 'User', '$2a$10$1pCaZ.GgVDGNG9aMsoIE/eLFOxf5mCpUFTnbhhBW0S7VPfyYKWbUG', 'norole@norole.com');

INSERT INTO user_role(user_id, role_id)
VALUES (1, 2);