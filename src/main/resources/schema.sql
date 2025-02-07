-- DROP TABLE IF EXISTS user_roles;
-- DROP TABLE IF EXISTS users;
-- DROP TABLE IF EXISTS roles;

CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(60) NOT NULL,
    UNIQUE INDEX idx_role_name (name)
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(120) NOT NULL,
    password VARCHAR(120) NOT NULL,
    phone VARCHAR(255) DEFAULT NULL,
    id_card VARCHAR(255) DEFAULT NULL,
    password_last_changed TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    password_expired BOOLEAN DEFAULT FALSE,
    UNIQUE INDEX idx_user_username (username),
    UNIQUE INDEX idx_user_email (email)
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    INDEX idx_user_roles_user_id (user_id),
    INDEX idx_user_roles_role_id (role_id)
); 