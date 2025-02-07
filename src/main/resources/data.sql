INSERT IGNORE INTO roles (name) VALUES ('ROLE_USER');
INSERT IGNORE INTO roles (name) VALUES ('ROLE_ADMIN');

-- 添加一个测试用户（密码是: Password123!）
INSERT IGNORE INTO users (username, email, password) 
VALUES ('testuser', 'test@example.com', '$2a$12$LQV3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LHHzGQVKUZRoPVVRi');

-- 给测试用户赋予 USER 角色
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'testuser' AND r.name = 'ROLE_USER'; 