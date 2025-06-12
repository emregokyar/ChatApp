CREATE DATABASE wp_clone;
USE wp_clone;

CREATE TABLE users(
	id INT PRIMARY KEY AUTO_INCREMENT,
	email VARCHAR(49) UNIQUE NOT NULL,
    password VARCHAR(49) NOT NULL,
    firstname VARCHAR(49) DEFAULT NULL,
    lastname VARCHAR(49) DEFAULT NULL,
    profile_photo VARCHAR(255) DEFAULT NULL,
	registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	is_private BOOLEAN NOT NULL DEFAULT FALSE
);	

CREATE TABLE channels(
	id INT UNIQUE PRIMARY KEY,
	updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE registered_channels(
	id INT PRIMARY KEY AUTO_INCREMENT,
    type ENUM('ADMIN', 'REGULAR') DEFAULT 'REGULAR',
	user_id INT NOT NULL,
    channel_id INT NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id),
    FOREIGN KEY(channel_id) REFERENCES channels(id)
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
    contacting_id INT NOT NULL,
    nickname VARCHAR(255),
    CHECK (contacter_id != contacting_id),
    FOREIGN KEY (contacter_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (contacting_id) REFERENCES users(id) ON DELETE CASCADE
);
