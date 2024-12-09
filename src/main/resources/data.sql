INSERT INTO categories (name, description, deleted, suggested) VALUES
 ('Event Planning', 'Category for organizing event-related tasks',  false, false),
 ('Catering', 'Food and beverages arrangements',   false, false),
 ('Venue Booking', 'Booking venues for events',   true, false),
 ('Photography', 'Photography and videography services',   false, false),
 ('Entertainment', 'Music, shows, and performances',   false, false),
 ('Logistics', 'Transport and materials management',   false, false),
 ('Decoration', 'Venue decoration and themes',   true, false),
 ('Security', 'Security and crowd control',   false, true),
 ('Guest Management', 'Handling guest invitations and RSVP',   false, false),
 ('Marketing', 'Promotions and event advertising',   false, false);

INSERT INTO events (name, description, date, privacy, max_participants, city, address, latitude, longitude) VALUES
 ('Tech Conference 2025', 'A conference for tech enthusiasts.', '2025-05-15', 'OPEN', 500, 'New York', '123 Event St.', 40.7128, -74.0060),
 ('Web Development Workshop', 'A workshop on modern web development techniques.', '2026-06-01', 'CLOSED', 50, 'Novi Sad', '45 Saviceva Street', 45.2671, 19.8335),
 ('Music Festival', 'An annual music festival with local and international bands.', '2027-07-10', 'OPEN', 1000, 'Novi Sad', '22 Park Road', 45.2675, 19.8338),
 ('Startup Pitch Event', 'A pitching event for emerging tech startups.', '2026-08-20', 'CLOSED', 100, 'Novi Sad', '10 Startup Blvd.', 45.2680, 19.8340),
 ('Film Screening', 'An exclusive screening of a new indie film.', '2024-09-05', 'OPEN', 200, 'Novi Sad', '88 Film Street', 45.2673, 19.8342),
 ('Art Exhibition', 'A contemporary art exhibition featuring local artists.', '2025-10-12', 'OPEN', 150, 'Novi Sad', '11 Exhibition Avenue', 45.2669, 19.8343),
 ('Gaming Tournament', 'A tournament for competitive video gamers.', '2026-11-01', 'CLOSED', 200, 'Novi Sad', '50 Gamer’s Lane', 45.2682, 19.8339),
 ('Tech Meetup 2025', 'A meetup for tech professionals to share knowledge and network.', '2025-03-15', 'OPEN', 300, 'Novi Sad', '30 Innovation Avenue', 45.2678, 19.8350);

INSERT INTO event_type (name, description, deleted) VALUES
('Wedding', 'Event type for organizing weddings', false),
('Corporate Event', 'Event type for organizing corporate events', false),
('Birthday Party', 'Event type for organizing birthdays', false);

INSERT INTO event_type_suggested_categories VALUES (1, 2),(1, 4),(1,9),(1,7),(2,1),(3,2),(3,4),(3, 7);

INSERT INTO products (id, name, description, specialties, price, discount, status, valid_from, is_available, is_deleted, is_visible, category_id) VALUES
 (nextval('solution_sequence'), 'Custom Invitations', 'Beautifully designed customizable invitations for all events', 'Invitations, Customizable', 2.50, 0.50, 'ACCEPTED', '2024-01-01 09:00:00', TRUE, FALSE, TRUE, 9),
 (nextval('solution_sequence'), 'Event Banner', 'High-quality banners for event promotion', 'Banners, Event', 50.00, 10.00, 'ACCEPTED', '2024-01-01 09:00:00', TRUE, FALSE, TRUE, 10),
 (nextval('solution_sequence'), 'Party Favors', 'Unique and personalized party favors for any occasion', 'Party, Favors', 1.50, 0.20, 'ACCEPTED', '2024-01-01 09:00:00', TRUE, FALSE, TRUE, 1),
 (nextval('solution_sequence'), 'Decorative Balloons', 'Colorful balloons for all events', 'Balloons, Decorative', 0.80, 0.10, 'ACCEPTED', '2024-01-01 09:00:00', TRUE, FALSE, TRUE, 7),
 (nextval('solution_sequence'), 'Event T-Shirts', 'Customizable t-shirts for event attendees', 'T-Shirts, Customizable', 15.00, 3.00, 'ACCEPTED', '2024-01-01 09:00:00', TRUE, FALSE, TRUE, 9),
 (nextval('solution_sequence'), 'Party Hats', 'Fun and colorful hats for parties and events', 'Party, Hats', 2.00, 0.50, 'ACCEPTED', '2024-01-01 09:00:00', TRUE, FALSE, TRUE, 7),
 (nextval('solution_sequence'), 'Event Mugs', 'Personalized mugs for event souvenirs', 'Mugs, Personalized', 5.00, 1.00, 'ACCEPTED', '2024-01-01 09:00:00', TRUE, FALSE, TRUE, 9),
 (nextval('solution_sequence'), 'Photo Frames', 'Customizable photo frames for event photos', 'Frames, Customizable', 8.00, 1.50, 'ACCEPTED', '2024-01-01 09:00:00', TRUE, FALSE, TRUE, 9);

INSERT INTO services (id, name, description, specialties, price, discount, status, valid_from, is_available, is_deleted, is_visible, type, reservation_deadline, cancellation_deadline, min_duration, max_duration, category_id) VALUES
 (nextval('solution_sequence'), 'Event Photography', 'Professional photography services for all types of events', 'Photography, Event', 150.00, 30.00, 'ACCEPTED', '2024-01-01 09:00:00', TRUE, FALSE, TRUE, 'MANUAL', '2024-05-01', '2024-04-15', 2, 6, 4),
 (nextval('solution_sequence'), 'Catering Service', 'Delicious and customizable catering for events', 'Catering, Customizable', 500.00, 50.00, 'ACCEPTED', '2024-01-01 09:00:00', TRUE, FALSE, TRUE, 'MANUAL', '2024-05-10', '2024-04-25', 3, 8, 2),
 (nextval('solution_sequence'), 'Event Planning', 'Comprehensive event planning services from start to finish', 'Event Planning, Full Service', 1200.00, 200.00, 'ACCEPTED', '2024-01-01 09:00:00', TRUE, FALSE, TRUE, 'MANUAL', '2024-06-01', '2024-05-15', 4, 10, 1),
 (nextval('solution_sequence'), 'Sound System Setup', 'High-quality sound system rental and setup for events', 'Sound System, Setup', 250.00, 40.00, 'ACCEPTED', '2024-01-01 09:00:00', TRUE, FALSE, TRUE, 'MANUAL', '2024-05-05', '2024-04-20', 1, 5, 5),
 (nextval('solution_sequence'), 'Decorative Lighting', 'Stunning lighting setups for all events', 'Lighting, Decorative', 300.00, 50.00, 'ACCEPTED', '2024-01-01 09:00:00', TRUE, FALSE, TRUE, 'MANUAL', '2024-04-25', '2024-04-10', 2, 4, 7),
 (nextval('solution_sequence'), 'Venue Booking', 'Booking service for event venues', 'Venue, Booking', 1000.00, 100.00, 'ACCEPTED', '2024-01-01 09:00:00', TRUE, FALSE, TRUE, 'MANUAL', '2024-06-15', '2024-06-01', 6, 12, 3),
 (nextval('solution_sequence'), 'Transportation Service', 'Event transportation services for guests and equipment', 'Transportation, Event', 350.00, 60.00, 'PENDING', '2024-01-01 09:00:00', TRUE, FALSE, TRUE, 'MANUAL', '2024-05-15', '2024-05-01', 3, 7, 8);

INSERT INTO reviews (creation_date, rating, feedback, status, solution_id) VALUES
 ('2024-12-01 09:00:00', 5, 'These invitations are beautifully designed and very easy to personalize! Perfect for our event.', 'ACCEPTED', 1),
 ('2024-12-02 10:30:00', 4, 'The banners are great, but the print quality could be better.', 'ACCEPTED', 2),
 ('2024-12-03 11:45:00', 3, 'The party favors are cute, but the variety is limited.', 'PENDING', 3),
 ('2024-12-01 14:00:00', 5, 'These balloons added the perfect pop of color to our event. Highly recommend!', 'ACCEPTED', 4),
 ('2024-12-01 12:15:00', 5, 'Everything is great!', 'ACCEPTED', 2),
 ('2024-12-03 18:20:00', 4, 'Nice mugs but the customization options are a bit limited.', 'ACCEPTED', 7),
 ('2024-12-02 13:00:00', 5, 'These photo frames were the perfect souvenir for our event. Everyone loved them!', 'ACCEPTED', 8),
 ('2024-12-03 11:45:00', 3, 'The event planner was okay, but some details were overlooked during the event.', 'PENDING', 11),
 ('2024-12-01 14:00:00', 5, 'Great sound system, the setup was flawless!', 'ACCEPTED', 12),
 ('2024-12-02 16:00:00', 4, 'The lighting setup was beautiful, but could have been a bit brighter.', 'ACCEPTED', 13);

-- Insert cities
INSERT INTO cities (name) VALUES
    ('Beograd'),
    ('Novi Sad'),
    ('Trebinje'),
    ('Sremska Mitrovica'),
    ('Kraljevo'),
    ('Kragujevac');

-- Insert users
INSERT INTO users (activated, city_id, role, suspended, activation_timestamp, id, address, email, lastname, name, password, phone_number, profile_picture) VALUES
   (true, 1, 4, false, '2024-12-07 12:00:00', 1, '123 Main St', 'organizer@gmail.com', 'Doe', 'John', '$2b$12$87YVdd.ESS6FJGh4JG6hf.psB4eqnvW2Ug/JmC8dvRALyUoAiTsIS', '123-456-7890', 'profile1.jpg'),
   (true, 2, 4, false, '2024-12-06 12:00:00', 2, '456 Elm St', 'jane.smith@example.com', 'Smith', 'Jane', '$2b$12$87YVdd.ESS6FJGh4JG6hf.psB4eqnvW2Ug/JmC8dvRALyUoAiTsIS', '987-654-3210', 'profile2.jpg'),
   (true, 3, 3, false, '2024-12-06 12:00:00', 3, '789 Oak St', 'provider@gmail.com', 'Johnson', 'Emily', '$2b$12$87YVdd.ESS6FJGh4JG6hf.psB4eqnvW2Ug/JmC8dvRALyUoAiTsIS', '555-123-4567', 'profile3.jpg'),
   (true, 4, 3, false, '2024-12-05 12:00:00', 4, '101 Pine St', 'michael.brown@example.com', 'Brown', 'Michael', '$2b$12$87YVdd.ESS6FJGh4JG6hf.psB4eqnvW2Ug/JmC8dvRALyUoAiTsIS', '111-222-3333', 'profile4.jpg'),
   (true, 5, 2, false, '2024-12-06 12:00:00', 5, '202 Maple St', 'admin@gmail.com', 'Davis', 'Sarah', '$2b$12$87YVdd.ESS6FJGh4JG6hf.psB4eqnvW2Ug/JmC8dvRALyUoAiTsIS', '444-555-6666', 'profile5.jpg');
