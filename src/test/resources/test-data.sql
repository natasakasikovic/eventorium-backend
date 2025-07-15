-- ROLES
INSERT INTO roles (id, name) VALUES (1, 'EVENT_ORGANIZER');

-- CITIES
INSERT INTO cities (id, name) VALUES (1, 'Beograd');

-- USERS
INSERT INTO users (id, verified, city_id, suspended, activation_timestamp, address, email, lastname, name, password, phone_number, last_password_reset, hash, profile_photo_id, deactivated, notifications_silenced) VALUES
 (1, true, 1,  null, '2024-12-07 12:00:00', 'Staparski put 18', 'organizer1@gmail.com', 'Doe', 'John', '$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W', '1234567890', '2017-10-01 21:58:58.508-07', '1', null, false, false),
 (2, true, 1,  null, '2024-12-06 12:00:00', 'Bulevar oslobodjenja, 20', 'organizer2@gmail.com', 'Smith', 'Jane', '$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W', '9876543210', '2017-10-01 21:58:58.508-07', '2', null, false, false),
 (3, true, 1,  null, '2024-12-06 12:00:00', 'Mise Dimitrijevica, 7', 'organizer3@gmail.com', 'Johnson', 'Emily', '$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W', '5551234567', '2017-10-01 21:58:58.508-07', '3', null, false, false);

-- EVENT_TYPES
INSERT INTO event_types (id, name, description, deleted, image_id) VALUES
(1, 'Wedding', 'Event type for organizing weddings', false, null);

-- EVENTS
INSERT INTO events (id, name, description, date, privacy, max_participants, type_id, address, city_id, organizer_id, is_draft, budget_id)
VALUES
(1, 'Wedding in Novi Sad', 'A beautiful wedding ceremony with reception and dance.', CURRENT_DATE + INTERVAL '3' DAY, 'OPEN', 100, 1, 'Bulevar Oslobođenja 12', 1, 1, false, null),
(2, 'Corporate Event in Novi Sad', 'A corporate networking event with speakers and workshops.', CURRENT_DATE + INTERVAL '4' DAY, 'OPEN', 50, 1, 'Futoška 25', 1, 1, false, null),
(3, 'Birthday Bash in Sombor', 'A fun-filled birthday party with music and food.', CURRENT_DATE + INTERVAL '5' DAY, 'OPEN', 30, 1, 'Trg Cara Uroša 6', 1, 1, false, null),
(4, 'Sombor Business Meetup', 'A professional business networking event in Sombor.', CURRENT_DATE + INTERVAL '3' DAY, 'OPEN', 40, 1, 'Čitaonička 15', 1, 2, false, null),
(5, 'Wedding Reception in Novi Sad', 'An elegant wedding reception with dinner and music.', CURRENT_DATE + INTERVAL '4' DAY, 'OPEN', 80, 1, 'Žarka Zrenjanina 48', 1, 2, false, null);

-- USER_BLOCKS
INSERT INTO user_blocks (id, blocker_id, blocked_id) VALUES (1, 3, 1);
