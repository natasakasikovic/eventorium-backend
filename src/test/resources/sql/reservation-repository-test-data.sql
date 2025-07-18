INSERT INTO cities (id, name) VALUES (1, 'Beograd');

INSERT INTO users (verified, city_id, suspended, activation_timestamp, address, email, lastname, name, password, phone_number, last_password_reset, hash, profile_photo_id, deactivated, notifications_silenced) VALUES
    (true, 1,  null, '2024-12-06 12:00:00', 'Mise Dimitrijevica, 7', 'provider@gmail.com', 'Johnson', 'Emily', '$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W', '5551234567', '2017-10-01 21:58:58.508-07', '3', null, false, false),
    (true, 1,  null, '2024-12-05 12:00:00', 'Venac Radomira Putnika 5', 'michael.brown@example.com', 'Brown', 'Michael', '$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W', '1112223333', '2017-10-01 21:58:58.508-07', '4', null, false, false);

INSERT INTO categories (id, name, description, deleted, suggested) VALUES
    (1, 'Event Planning', 'Category for organizing event-related tasks',  false, false),
    (2, 'Catering', 'Food and beverages arrangements',   false, false),
    (4, 'Photography', 'Photography and videography services',   false, false);


INSERT INTO event_types (name, description, deleted, image_id) VALUES
    ('Wedding', 'Event type for organizing weddings', false, null),
    ('Corporate Event', 'Event type for organizing corporate events', false, null),
    ('Birthday Party', 'Event type for organizing birthdays', false, null);

INSERT INTO services (id, name, description, specialties, price, discount, status, is_available, is_deleted, is_visible, type, reservation_deadline, cancellation_deadline, min_duration, max_duration, category_id, provider_id) VALUES
    (nextval('solution_sequence'), 'Event Photography', 'Professional photography services for all types of events', 'Photography, Event', 150.00, 30.00, 'ACCEPTED', TRUE, FALSE, TRUE, 'MANUAL', 2, 3, 2, 6, 4, 1),
    (nextval('solution_sequence'), 'Catering Service', 'Delicious and customizable catering for events', 'Catering, Customizable', 500.00, 50.00, 'ACCEPTED', TRUE, FALSE, TRUE, 'MANUAL', 1, 5, 3, 8, 2, 1),
    (nextval('solution_sequence'), 'Event Planning', 'Comprehensive event planning services from start to finish', 'Event Planning, Full Service', 1200.00, 0.00, 'ACCEPTED', TRUE, FALSE, TRUE, 'MANUAL', 3, 10, 4, 10, 1, 2);

INSERT INTO events (name, description, date, privacy, max_participants, type_id, address, city_id, organizer_id, is_draft, budget_id) VALUES
    ('Wedding in Novi Sad', 'A beautiful wedding ceremony with reception and dance.', CURRENT_DATE + INTERVAL '3' DAY, 'OPEN', 100, 1, 'Bulevar Oslobođenja 12', 1, 1, false, null),
    ('Corporate Event in Novi Sad', 'A corporate networking event with speakers and workshops.', CURRENT_DATE + INTERVAL '4' DAY, 'OPEN', 50, 2, 'Futoška 25', 1, 2, false, null),
    ('Birthday Bash in Sombor', 'A fun-filled birthday party with music and food.', CURRENT_DATE + INTERVAL '5' DAY, 'OPEN', 30, 3, 'Trg Cara Uroša 6', 1, 1, false, null),
    ('Sombor Business Meetup', 'A professional business networking event in Sombor.', CURRENT_DATE + INTERVAL '3' DAY, 'OPEN', 40, 2, 'Čitaonička 15', 1, 2, false, null),
    ('Wedding Reception in Novi Sad', 'An elegant wedding reception with dinner and music.', CURRENT_DATE + INTERVAL '4' DAY, 'OPEN', 80, 1, 'Žarka Zrenjanina 48',1, 2, false, null);

INSERT INTO service_reservations (ending_time, is_canceled, starting_time, event_id, service_id, status)
VALUES
    ('12:00:00', False, '10:00:00', 1, 1, 'DECLINED'),
    ('21:00:00', False, '19:00:00', 1, 3, 'PENDING'),
    ('13:00:00', False, '15:00:00', 2, 1, 'ACCEPTED'),
    ('19:00:00', False, '17:00:00', 1, 1, 'ACCEPTED');