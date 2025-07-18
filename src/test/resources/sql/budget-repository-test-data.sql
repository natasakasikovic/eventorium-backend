INSERT INTO cities (id, name) VALUES (1, 'Beograd');

INSERT INTO categories (id, name, description, deleted, suggested) VALUES
    (1, 'Event Planning', 'Category for organizing event-related tasks', FALSE, FALSE),
    (2, 'Guest Management', 'Handling guest invitations and RSVP', FALSE, FALSE),
    (3, 'Marketing', 'Promotions and event advertising', FALSE, FALSE);

INSERT INTO users (id, verified, city_id, suspended, activation_timestamp, address, email, lastname, name, password, phone_number, last_password_reset, hash, profile_photo_id, deactivated)
VALUES
    (1, TRUE, 1, NULL, '2024-12-07 12:00:00', 'Staparski put 18', 'organizer@gmail.com', 'Doe', 'John', '$2a$10$dummyhash', '1234567890', '2017-10-01 21:58:58', '1', NULL, FALSE),
    (2, TRUE, 1, NULL, '2024-12-07 12:00:00', 'Staparski put 18', 'organizer2@gmail.com', 'Doe', 'John', '$2a$10$dummyhash', '1234567890', '2017-10-01 21:58:58', '2', NULL, FALSE),
    (3, TRUE, 1, NULL, '2024-12-07 12:00:00', 'Staparski put 18', 'provider@gmail.com', 'Doe', 'John', '$2a$10$dummyhash', '1234567890', '2017-10-01 21:58:58', '3', NULL, FALSE);

INSERT INTO products (id, name, description, price, discount, status, is_available, is_deleted, is_visible, category_id, provider_id) VALUES
    (1, 'Custom Invitations', 'Beautifully designed customizable invitations for all events', 30.00, 50.00, 'ACCEPTED', TRUE, FALSE, TRUE, 1, 3),
    (2, 'Event Banner', 'High-quality banners for event promotion', 50.00, 10.00, 'ACCEPTED', TRUE, FALSE, TRUE, 2, 3),
    (3, 'Party Favors', 'Unique and personalized party favors for any occasion', 20.0, 0.00, 'ACCEPTED', TRUE, FALSE, TRUE, 3, 3),
    (4, 'Decorative Balloons', 'Colorful balloons for all events', 10.00, 0.00, 'ACCEPTED', TRUE, FALSE, TRUE, 1, 3);

INSERT INTO services (id, name, description, specialties, price, discount, status, is_available, is_deleted, is_visible, type, reservation_deadline, cancellation_deadline, min_duration, max_duration, category_id, provider_id) VALUES
    (5, 'Event Photography', 'Professional photography services for all types of events', 'Photography, Event', 150.00, 30.00, 'ACCEPTED', TRUE, FALSE, TRUE, 'MANUAL', 14, 3, 2, 6, 1, 3),
    (6, 'Catering Service', 'Delicious and customizable catering for events', 'Catering, Customizable', 500.00, 50.00, 'ACCEPTED', TRUE, FALSE, TRUE, 'MANUAL', 21, 5, 3, 8, 2, 3),
    (7, 'Event Planning', 'Comprehensive event planning services from start to finish', 'Event Planning, Full Service', 1200.00, 0.00, 'ACCEPTED', TRUE, FALSE, TRUE, 'MANUAL', 30, 10, 4, 10, 3, 3);

INSERT INTO budgets (id, planned_amount, spent_amount) VALUES
    (1, 85.0, 80.0),
    (2, 85.0, 80.0),
    (3, 85.0, 80.0);

-- Budget Items (linked to correct solutions)
INSERT INTO budget_items (id, planned_amount, category_id, processed_at, solution_id, item_type, status) VALUES
    (1, 20.0, 1, CURRENT_DATE, 1, 'PRODUCT', 'PROCESSED'),
    (2, 45.0, 2, CURRENT_DATE, 2, 'PRODUCT', 'PROCESSED'),
    (3, 20.0, 3, CURRENT_DATE, 3, 'PRODUCT', 'PROCESSED'),
    (4, 20.0, 3, CURRENT_DATE, 3, 'PRODUCT', 'PROCESSED'),
    (5, 20.0, 1, CURRENT_DATE, 1, 'PRODUCT', 'PROCESSED'),
    (6, 20.0, 1, CURRENT_DATE, 1, 'PRODUCT', 'PROCESSED'),
    (7, 20.0, 1, CURRENT_DATE, 1, 'PRODUCT', 'PROCESSED');

INSERT INTO budgets_items (budget_id, items_id) VALUES
    (1, 1),
    (1, 2),
    (1, 3),
    (2, 4),
    (2, 5),
    (3, 6),
    (3, 7);

INSERT INTO event_types (name, description, deleted) VALUES
    ('Wedding', 'Event type for organizing weddings', false);

INSERT INTO events (id, name, description, date, privacy, max_participants, type_id, address, city_id, organizer_id, is_draft, budget_id)
VALUES
    (1, 'Wedding in Novi Sad', 'Wedding ceremony', CURRENT_DATE + INTERVAL '3' DAY, 'OPEN', 100, 1, '123 Wedding St', 1, 1, FALSE, 1),
    (2, 'Wedding in Novi Sad', 'Wedding ceremony', CURRENT_DATE + INTERVAL '3' DAY, 'OPEN', 100, 1, '123 Wedding St', 1, 2, FALSE, 2),
    (3, 'Wedding in Novi Sad', 'Wedding ceremony', CURRENT_DATE + INTERVAL '3' DAY, 'OPEN', 100, 1, '123 Wedding St', 1, 2, FALSE, 3);
