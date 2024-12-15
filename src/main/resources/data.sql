INSERT INTO roles (name) VALUES ('USER');
INSERT INTO roles (name) VALUES ('UNAUTHENTICATED_USER');
INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('PROVIDER');
INSERT INTO roles (name) VALUES ('EVENT_ORGANIZER');

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

INSERT INTO cities (name) VALUES
    ('Beograd'),
    ('Novi Sad'),
    ('Trebinje'),
    ('Sremska Mitrovica'),
    ('Kraljevo'),
    ('Kragujevac');

INSERT INTO users (activated, city_id, suspended, activation_timestamp, id, address, email, lastname, name, password, phone_number, profile_picture, last_password_reset) VALUES
   (true, 1,  false, '2024-12-07 12:00:00', 1, '123 Main St', 'organizer@gmail.com', 'Doe', 'John', '$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W', '123-456-7890', 'profile1.jpg', '2017-10-01 21:58:58.508-07'),
   (true, 2,  false, '2024-12-06 12:00:00', 2, '456 Elm St', 'jane.smith@example.com', 'Smith', 'Jane', '$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W', '987-654-3210', 'profile2.jpg', '2017-10-01 21:58:58.508-07'),
   (true, 3,  false, '2024-12-06 12:00:00', 3, '789 Oak St', 'provider@gmail.com', 'Johnson', 'Emily', '$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W', '555-123-4567', 'profile3.jpg', '2017-10-01 21:58:58.508-07'),
   (true, 4,  false, '2024-12-05 12:00:00', 4, '101 Pine St', 'michael.brown@example.com', 'Brown', 'Michael', '$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W', '111-222-3333', 'profile4.jpg', '2017-10-01 21:58:58.508-07'),
   (true, 5,  false, '2024-12-06 12:00:00', 5, '202 Maple St', 'admin@gmail.com', 'Davis', 'Sarah', '$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W', '444-555-6666', 'profile5.jpg', '2017-10-01 21:58:58.508-07');

INSERT INTO USER_ROLE (user_id, role_id) VALUES (1, 5);
INSERT INTO USER_ROLE (user_id, role_id) VALUES (2, 5);
INSERT INTO USER_ROLE (user_id, role_id) VALUES (3, 4);
INSERT INTO USER_ROLE (user_id, role_id) VALUES (4, 4);
INSERT INTO USER_ROLE (user_id, role_id) VALUES (5, 3);



INSERT INTO events (name, description, date, privacy, max_participants, type_id, address, city_id, organizer_id)
VALUES
    ('John and Janes Wedding', 'A beautiful celebration of love and commitment.', '2024-06-15', 'CLOSED', 150, 1, 'Rose Garden Venue, Central Park', 1, 1),
    ('Tech Summit 2024', 'A gathering of tech enthusiasts to explore new trends and innovations.', '2024-03-20', 'OPEN', 500, 2, 'Grand Conference Hall, Tech City', 3, 1),
    ('Emilys 30th Birthday Bash', 'A fun-filled birthday party with games and live music.', '2024-07-05', 'CLOSED', 50, 3, 'Banquet Hall, Riverside', 2, 1),
    ('Corporate Annual Meet', 'Yearly gathering of the companys employees and stakeholders.', '2024-04-10', 'CLOSED', 300, 2, 'Downtown Business Center, Metropolis', 4, 1),
    ('Outdoor Wedding Celebration', 'A romantic outdoor wedding under the stars.', '2024-08-12', 'CLOSED', 200, 1, 'Meadow Fields, Green Valley', 5, 1);


INSERT INTO activities (name, description, start_time, end_time, location, event_id)
VALUES
    ( 'Wedding Ceremony', 'Exchange of vows and official marriage proceedings.', '15:00:00', '16:00:00', 'Rose Garden Venue', 1),
    ( 'Cocktail Reception', 'A relaxed gathering with drinks and appetizers.', '16:30:00', '18:00:00', 'Garden Terrace', 1),
    ( 'Wedding Dinner', 'Formal dinner for the wedding guests.', '18:30:00', '21:00:00', 'Grand Ballroom',1 ),
    ( 'Dance Party', 'Dancing and celebration with live music.', '21:30:00', '23:59:00', 'Ballroom Dance Floor', 2),
    ( 'After Party', 'Late-night gathering for close friends and family.', '00:30:00', '02:00:00', 'Private Lounge', 2),
    ( 'Opening Keynote', 'Kick-off presentation by a renowned speaker.', '09:00:00', '10:00:00', 'Main Auditorium', 2),
    ( 'Panel Discussion', 'Experts discuss the latest trends and innovations.', '10:15:00', '11:45:00', 'Conference Hall B', 2),
    ( 'Networking Lunch', 'Informal lunch to connect with other attendees.', '12:00:00', '13:30:00', 'Dining Hall', 2),
    ( 'Workshop: AI in Practice', 'Hands-on session on practical AI applications.', '14:00:00', '16:00:00', 'Workshop Room 3', 3),
    ( 'Closing Ceremony', 'Summary and thank-you session for participants.', '16:30:00', '17:00:00', 'Main Auditorium', 3),
    ( 'Welcome Guests', 'Guests arrive and enjoy light refreshments.', '18:00:00', '18:30:00', 'Main Hall Entrance', 3),
    ( 'Birthday Speech', 'Host delivers a speech to thank guests.', '18:45:00', '19:00:00', 'Banquet Hall', 3),
    ( 'Dinner and Cake Cutting', 'Formal dinner followed by cutting the birthday cake.', '19:15:00', '21:00:00', 'Dining Area', 3),
    ( 'Games and Entertainment', 'Fun games and entertainment for all guests.', '21:15:00', '22:30:00', 'Activity Room', 3);

INSERT INTO event_classifications (solution_id, event_type_id) VALUES
(1, 1), (1, 2), (1, 3), (2, 1), (2, 2), (3, 3), (4, 1), (4, 2), (5, 1), (5, 2),
(6, 3), (7, 1), (7, 2), (8, 3), (9, 1), (10, 2), (11, 3), (12, 1), (13, 2),
(14, 3), (15, 1);

INSERT INTO images (content_type, path) VALUES
('image/jpeg', 'image1.jpg'),
('image/jpeg', 'image2.jpg'),
('image/jpeg', 'image3.jpg'),
('image/jpg', 'image4.jpg'),
('image/png', 'image5.png');

INSERT INTO solution_image_paths (image_paths_id, solution_id) VALUES
(1, 14),
(2, 14),
(3, 14),
(4, 14),
(5, 12);






