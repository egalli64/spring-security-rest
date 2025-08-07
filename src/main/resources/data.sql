/*
 * Spring Boot Security tutorial 
 * 
 * https://github.com/egalli64/spring-security
 */

drop table if exists user_authorities;
drop table if exists authorities;
drop table if exists user_roles;
drop table if exists roles;
drop table if exists users;

--
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT true,
    account_expired BOOLEAN NOT NULL DEFAULT false,
    account_locked BOOLEAN NOT NULL DEFAULT false,
    credentials_expired BOOLEAN NOT NULL DEFAULT false
);

-- passwords are BCrypt encoded (password, admin, super, disabled)
INSERT INTO users (username, password, enabled, account_expired, account_locked, credentials_expired) VALUES
('user', '$2a$10$Nz6HnrrNtZaq7geejWO/UeBGx1XBuCYPkEQYSougOC3hsMWU4bP8O', true, false, false, false),
('admin', '$2a$10$P8wz0QHgXsx26dT0EhYcSe/Q5puYlZ6GVn97b9CHumoHLByav1Rq.', true, false, false, false),
('admin2', '$2a$10$P8wz0QHgXsx26dT0EhYcSe/Q5puYlZ6GVn97b9CHumoHLByav1Rq.', true, false, false, false),
('superuser', '$2a$10$SorlL/lHnLySkw9XVts7Lep5jgf2ZiItEK.rZC03fTFOrEmrIEm3K', true, false, false, false),
('disabled', '$2a$10$hNdqUfpjggSoiVGpVfL8k.QfMOVPC0B/cditiZVLCkwNBS2qChUQO', false, false, false, false);

--
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

INSERT INTO roles (name) VALUES ('USER'), ('ADMIN');

--
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

INSERT INTO user_roles (user_id, role_id) VALUES
((SELECT id FROM users WHERE username = 'user'), (SELECT id FROM roles WHERE name = 'USER')),
((SELECT id FROM users WHERE username = 'admin'), (SELECT id FROM roles WHERE name = 'ADMIN')),
((SELECT id FROM users WHERE username = 'admin2'), (SELECT id FROM roles WHERE name = 'ADMIN')),
((SELECT id FROM users WHERE username = 'superuser'), (SELECT id FROM roles WHERE name = 'USER')),
((SELECT id FROM users WHERE username = 'superuser'), (SELECT id FROM roles WHERE name = 'ADMIN')),
((SELECT id FROM users WHERE username = 'disabled'), (SELECT id FROM roles WHERE name = 'USER'));

--
CREATE TABLE authorities (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) UNIQUE NOT NULL
);

INSERT INTO authorities (name) VALUES ('VIEW_REPORTS'), ('EDIT_POSTS');

--
CREATE TABLE user_authorities (
  user_id BIGINT NOT NULL,
  authority_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, authority_id),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (authority_id) REFERENCES authorities(id) ON DELETE CASCADE
);

INSERT INTO user_authorities (user_id, authority_id) VALUES
((SELECT id FROM users WHERE username = 'admin'), (SELECT id FROM authorities WHERE name = 'VIEW_REPORTS'));
