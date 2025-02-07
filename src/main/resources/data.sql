INSERT INTO roles (name) VALUES ('ROLE_USER') ON DUPLICATE KEY UPDATE name = name;
INSERT INTO roles (name) VALUES ('ROLE_ADMIN') ON DUPLICATE KEY UPDATE name = name;

-- 添加一个测试用户（密码是: Password123!）
INSERT INTO users (username, email, password) 
VALUES ('testuser', 'test@example.com', '$2a$12$LQV3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LHHzGQVKUZRoPVVRi')
ON DUPLICATE KEY UPDATE username = username;

-- 给测试用户赋予 USER 角色
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'testuser' AND r.name = 'ROLE_USER'
ON DUPLICATE KEY UPDATE user_id = user_id; 