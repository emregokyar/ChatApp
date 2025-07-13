INSERT INTO users (id, username, registration_type, full_name, profile_photo, about, is_private, is_active, activation_number)
	VALUES
	(2, 'alice@example.com', 'EMAIL', 'Alice Johnson', 'test.jpg', 'Loves cats and coding', FALSE, TRUE, NULL),
	(3, 'bob@example.com', 'EMAIL', 'Bob Smith', 'test.png', 'Cyclist and chef', TRUE, TRUE, NULL),
	(4, '+905555555555', 'PHONE', 'Carol White', NULL, 'Traveler and writer', FALSE, TRUE, NULL);

INSERT INTO channels (id, group_photo, subject, type)
	VALUES
	(1, 'test.jpg', 'Weekend Plans', 'GROUP'),
	(2, NULL, 'Test Group', 'PRIVATE');

INSERT INTO group_roles (id, role)
	VALUES
	(1, 'ADMIN'),
	(2, 'REGULAR');

INSERT INTO registered_channels (id, user_id, channel_id, role_id)
	VALUES
	(1, 2, 1, 1),  -- Alice as ADMIN in GROUP
	(2, 3, 1, 2),  -- Bob as REGULAR in GROUP
	(3, 2, 2, 1),  -- Alice in PRIVATE
	(4, 4, 2, 2);  -- Carol in PRIVATE

INSERT INTO messages (id, content, type, sender_id, channel_id)
	VALUES
	(1, 'Hey team! Are we meeting Saturday?', 'TEXT', 2, 1),
	(2, 'Yes, Saturday works.', 'TEXT', 3, 1),
	(3, 'Sharing the doc', 'TEXT', 2, 1),
	(4, 'Hey Carol!', 'TEXT', 2, 2),
	(5, 'Hi Alice!', 'TEXT', 4, 2);

INSERT INTO contacts (contacter_id, contacting_id, nickname)
    VALUES
    (2, 3, 'Bob'),
    (2, 4, 'Carol'),
    (3, 2, 'Alice'),
    (4, 2, 'Ali');