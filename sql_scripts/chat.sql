DROP DATABASE IF EXISTS wp_clone;
CREATE DATABASE wp_clone;
USE wp_clone;

CREATE TABLE users(
	id INT PRIMARY KEY AUTO_INCREMENT,
	username VARCHAR(49) UNIQUE NOT NULL,
	registration_type ENUM('PHONE', 'EMAIL') NOT NULL,
    full_name VARCHAR(49) DEFAULT NULL,
    profile_photo VARCHAR(255) DEFAULT NULL,
    about VARCHAR(255) DEFAULT NULL,
	registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	is_private BOOLEAN NOT NULL DEFAULT FALSE,
	is_active BOOLEAN NOT NULL DEFAULT FALSE,
    activation_number VARCHAR(8) UNIQUE DEFAULT NULL
);

CREATE TABLE login_tokens(
    id INT PRIMARY KEY AUTO_INCREMENT,
	token VARCHAR(49) NOT NULL,
	expiration_date TIMESTAMP NOT NULL,
    user_id INT NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE channels(
    id INT PRIMARY KEY AUTO_INCREMENT,
	updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	group_photo VARCHAR(255) DEFAULT NULL,
    subject VARCHAR(255) DEFAULT NULL
);

CREATE TABLE group_roles(
    id INT PRIMARY KEY AUTO_INCREMENT,
    role ENUM('ADMIN', 'REGULAR') NOT NULL UNIQUE
);

-- Give a role to user in the registered channel
CREATE TABLE registered_channels(
	id INT PRIMARY KEY AUTO_INCREMENT,
	user_id INT NOT NULL,
    channel_id INT NOT NULL,
    role_id INT NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id),
    FOREIGN KEY(channel_id) REFERENCES channels(id),
    FOREIGN KEY(role_id) REFERENCES group_roles(id)
);

CREATE TABLE messages(
	id INT PRIMARY KEY AUTO_INCREMENT,
    content VARCHAR(1024),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    type ENUM('PHOTO', 'TEXT', 'FILE', 'LOCATION') NOT NULL DEFAULT 'TEXT',
	sender_id INT NOT NULL,
	channel_id INT NOT NULL,
    FOREIGN KEY(sender_id) REFERENCES users(id),
	FOREIGN KEY(channel_id) REFERENCES channels(id)	
);

CREATE TABLE contacts (
	id INT PRIMARY KEY AUTO_INCREMENT,
    contacter_id INT NOT NULL,
    contacting_id INT,
    nickname VARCHAR(255),
    CHECK (contacter_id != contacting_id),
    FOREIGN KEY (contacter_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (contacting_id) REFERENCES users(id) ON DELETE CASCADE
);