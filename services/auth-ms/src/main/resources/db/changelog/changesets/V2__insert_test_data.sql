INSERT INTO roles (role_name)
VALUES ('ROLE_ADMIN'),
       ('ROLE_USER');

INSERT INTO auth_users (username, password, is_active)
VALUES ('admin1', '$2a$10$CCqJSRQPh9ZAA5WXpICRpekyLAFIlW0A35gGrZ.1eVXMSOgXtZfOi', true),
       ('user1', '$2a$10$CCqJSRQPh9ZAA5WXpICRpekyLAFIlW0A35gGrZ.1eVXMSOgXtZfOi', true),
       ('user2', '$2a$10$CCqJSRQPh9ZAA5WXpICRpekyLAFIlW0A35gGrZ.1eVXMSOgXtZfOi', true),
       ('manager1', '$2a$10$CCqJSRQPh9ZAA5WXpICRpekyLAFIlW0A35gGrZ.1eVXMSOgXtZfOi', true),
       ('inactive_user', '$2a$10$CCqJSRQPh9ZAA5WXpICRpekyLAFIlW0A35gGrZ.1eVXMSOgXtZfOi', false);

INSERT INTO auth_user_roles (user_id, role_id)
VALUES ((SELECT id FROM auth_users WHERE username = 'admin1'), 1),
       ((SELECT id FROM auth_users WHERE username = 'admin1'), 2),
       ((SELECT id FROM auth_users WHERE username = 'user1'), 2),
       ((SELECT id FROM auth_users WHERE username = 'user2'), 2),
       ((SELECT id FROM auth_users WHERE username = 'manager1'), 1),
       ((SELECT id FROM auth_users WHERE username = 'manager1'), 2),
       ((SELECT id FROM auth_users WHERE username = 'inactive_user'), 1);