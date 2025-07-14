INSERT INTO roles (name) VALUES
('USER'),
('UNAUTHENTICATED_USER'),
('ADMIN'),
('PROVIDER'),
('EVENT_ORGANIZER');

INSERT INTO cities (name) VALUES
('Beograd'),
('Novi Sad'),
('Trebinje'),
('Sremska Mitrovica'),
('Kraljevo'),
('Sombor'),
('Kragujevac');

INSERT INTO users (verified, city_id, suspended, activation_timestamp, address, email, lastname, name, password, phone_number, last_password_reset, hash, profile_photo_id, deactivated) VALUES
(true, 6,  null, '2024-12-07 12:00:00', 'Staparski put 18', 'organizer@gmail.com', 'Doe', 'John', '$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W', '1234567890', '2017-10-01 21:58:58.508-07', '1', null, false),
(true, 2,  null, '2024-12-06 12:00:00', 'Mise Dimitrijevica, 7', 'provider@gmail.com', 'Johnson', 'Emily', '$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W', '5551234567', '2017-10-01 21:58:58.508-07', '3', null, false),
(true, 1,  null, '2024-12-06 12:00:00', 'Njegoševa 12', 'admin@gmail.com', 'Davis', 'Sarah', '$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W', '4445556666', '2017-10-01 21:58:58.508-07', '5', null, false),
(true, 6,  null, '2024-12-07 12:00:00', 'Staparski put 18', 'organizer2@gmail.com', 'Doe', 'John', '$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W', '1234567890', '2017-10-01 21:58:58.508-07', '2', null, false),
(true, 6,  null, '2024-12-07 12:00:00', 'Staparski put 18', 'organizer3@gmail.com', 'Doe', 'John', '$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W', '1234567890', '2017-10-01 21:58:58.508-07', '4', null, false),
(true, 6,  null, '2024-12-07 12:00:00', 'Staparski put 18', 'organizernoevents@gmail.com', 'Doe', 'John', '$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W', '1234567890', '2017-10-01 21:58:58.508-07', '6', null, false);

INSERT INTO USER_ROLE (user_id, role_id) VALUES (1, 5), (2, 4), (3, 3), (4,5), (5,5), (6,5);

INSERT INTO categories (name, description, deleted, suggested) VALUES
    ('Event Planning', 'Category for organizing event-related tasks',  false, false),
    ('Catering', 'Food and beverages arrangements',   false, false),
    ('Venue Booking', 'Booking venues for events',   false, false),
    ('Photography', 'Photography and videography services',   false, false),
    ('Entertainment', 'Music, shows, and performances',   false, false),
    ('Logistics', 'Transport and materials management',   false, false),
    ('Decoration', 'Venue decoration and themes',   false, false),
    ('Security', 'Security and crowd control',   false, true),
    ('Guest Management', 'Handling guest invitations and RSVP',   false, false),
    ('Marketing', 'Promotions and event advertising',   false, false);

INSERT INTO event_types (name, description, deleted) VALUES
    ('Wedding', 'Event type for organizing weddings', false),
    ('Corporate Event', 'Event type for organizing corporate events', false),
    ('Birthday Party', 'Event type for organizing birthdays', false);

INSERT INTO budgets VALUES (85.0, 80.0), (20.0, 20.0), (20.0, 20.0), (20.0, 20.0), (20.0, 20.0), (0.0,0.0);

INSERT INTO budget_items (planned_amount, category_id, processed_at, solution_id, item_type, status) VALUES
    (20.0,9,CURRENT_DATE, 1, 'PRODUCT', 'PROCESSED'),
    (45.0, 10, CURRENT_DATE, 2, 'PRODUCT', 'PROCESSED'),
    (20.0,1, CURRENT_DATE, 3, 'PRODUCT', 'PROCESSED'),

    (20.0,1, CURRENT_DATE, 3, 'PRODUCT', 'PROCESSED'),

    (20.0,9, CURRENT_DATE, 1, 'PRODUCT', 'PROCESSED'),

    (20.0,9, CURRENT_DATE, 1, 'PRODUCT', 'PROCESSED'),

    (20.0,9, CURRENT_DATE, 1, 'PRODUCT', 'PROCESSED');

INSERT INTO budgets_items VALUES (1,1), (1,2), (1,3), (2,4), (3,5), (4,6), (5,7);

INSERT INTO events (name, description, date, privacy, max_participants, type_id, address, city_id, organizer_id, is_draft, budget_id)
VALUES
    ('Wedding in Novi Sad', 'A beautiful wedding ceremony with reception and dance.', CURRENT_DATE + INTERVAL '3' DAY, 'OPEN', 100, 1, '123 Wedding St', 2, 1, true, 1),
    ('Corporate Event in Novi Sad', 'A corporate networking event with speakers and workshops.', CURRENT_DATE + INTERVAL '4' DAY, 'OPEN', 50, 2, '456 Business Ave', 2, 4, false, 2),
    ('Birthday Bash in Sombor', 'A fun-filled birthday party with music and food.', CURRENT_DATE + INTERVAL '5' DAY, 'OPEN', 30, 3, '789 Birthday Blvd', 6, 4, false, 3),
    ('Sombor Business Meetup', 'A professional business networking event in Sombor.', CURRENT_DATE + INTERVAL '3' DAY, 'OPEN', 40, 2, '234 Business Rd', 6, 5, false, 4),
    ('Wedding Reception in Novi Sad', 'An elegant wedding reception with dinner and music.', CURRENT_DATE + INTERVAL '4' DAY, 'OPEN', 80, 1, '321 Reception St', 2, 1, false, 5),
    ('Birthday Celebration in Beograd', 'A lively birthday party with a band and dancing.', CURRENT_DATE + INTERVAL '5' DAY, 'OPEN', 150, 3, '101 Celebration Ave', 1, 1, true, 6),
    ('My birthday!!!', 'A lively birthday party with a band and dancing.', CURRENT_DATE + INTERVAL '10' DAY, 'OPEN', 150, 3, '101 Celebration Ave', 1, 1, true, null);

INSERT INTO activities (name, description, start_time, end_time, location, event_id)
VALUES
    ( 'Wedding Ceremony', 'Exchange of vows and official marriage proceedings.', '15:00:00', '16:00:00', 'Rose Garden Venue', 6),
    ( 'Cocktail Reception', 'A relaxed gathering with drinks and appetizers.', '16:30:00', '18:00:00', 'Garden Terrace', 6);

INSERT INTO products (id, name, description, price, discount, status, is_available, is_deleted, is_visible, category_id, provider_id) VALUES
    (nextval('solution_sequence'), 'Custom Invitations', 'Beautifully designed customizable invitations for all events', 30.00, 50.00, 'ACCEPTED', TRUE, FALSE, TRUE, 9, 3),
    (nextval('solution_sequence'), 'Event Banner', 'High-quality banners for event promotion', 50.00, 10.00, 'ACCEPTED', TRUE, FALSE, TRUE, 10, 3),
    (nextval('solution_sequence'), 'Party Favors', 'Unique and personalized party favors for any occasion', 20.0, 0.00, 'ACCEPTED', TRUE, FALSE, TRUE, 1, 3),
    (nextval('solution_sequence'), 'Decorative Balloons', 'Colorful balloons for all events', 10.00, 0.00, 'ACCEPTED', TRUE, FALSE, TRUE, 7, 3);

INSERT INTO services (id, name, description, specialties, price, discount, status, is_available, is_deleted, is_visible, type, reservation_deadline, cancellation_deadline, min_duration, max_duration, category_id, provider_id) VALUES
    (nextval('solution_sequence'), 'Event Photography', 'Professional photography services for all types of events', 'Photography, Event', 150.00, 30.00, 'ACCEPTED', TRUE, FALSE, TRUE, 'MANUAL', 14, 3, 2, 6, 4, 3),
    (nextval('solution_sequence'), 'Catering Service', 'Delicious and customizable catering for events', 'Catering, Customizable', 500.00, 50.00, 'ACCEPTED', TRUE, FALSE, TRUE, 'MANUAL', 21, 5, 3, 8, 2, 3),
    (nextval('solution_sequence'), 'Event Planning', 'Comprehensive event planning services from start to finish', 'Event Planning, Full Service', 1200.00, 0.00, 'ACCEPTED', TRUE, FALSE, TRUE, 'MANUAL', 30, 10, 4, 10, 1, 3);