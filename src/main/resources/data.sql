INSERT INTO roles (name) VALUES ('USER');
INSERT INTO roles (name) VALUES ('UNAUTHENTICATED_USER');
INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('PROVIDER');
INSERT INTO roles (name) VALUES ('EVENT_ORGANIZER');

INSERT INTO cities (name) VALUES
('Beograd'),
('Novi Sad'),
('Trebinje'),
('Sremska Mitrovica'),
('Kraljevo'),
('Sombor'),
('Kragujevac');

INSERT INTO users (activated, city_id, suspended, activation_timestamp, address, email, lastname, name, password, phone_number, profile_photo, last_password_reset, hash) VALUES
                                (true, 6,  false, '2024-12-07 12:00:00', '123 Main St', 'organizer@gmail.com', 'Doe', 'John', '$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W', '123-456-7890', null, '2017-10-01 21:58:58.508-07', '1'),
                                (true, 2,  false, '2024-12-06 12:00:00', '456 Elm St', 'jane.smith@example.com', 'Smith', 'Jane', '$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W', '987-654-3210', null, '2017-10-01 21:58:58.508-07', '2'),
                                (true, 2,  false, '2024-12-06 12:00:00', '789 Oak St', 'provider@gmail.com', 'Johnson', 'Emily', '$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W', '555-123-4567', null, '2017-10-01 21:58:58.508-07', '3'),
                                (true, 6,  false, '2024-12-05 12:00:00', '101 Pine St', 'michael.brown@example.com', 'Brown', 'Michael', '$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W', '111-222-3333', null, '2017-10-01 21:58:58.508-07', '4'),
                                (true, 1,  false, '2024-12-06 12:00:00', '202 Maple St', 'admin@gmail.com', 'Davis', 'Sarah', '$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W', '444-555-6666', null, '2017-10-01 21:58:58.508-07', '5');

INSERT INTO USER_ROLE (user_id, role_id) VALUES (1, 5);
INSERT INTO USER_ROLE (user_id, role_id) VALUES (2, 5);
INSERT INTO USER_ROLE (user_id, role_id) VALUES (3, 4);
INSERT INTO USER_ROLE (user_id, role_id) VALUES (4, 4);
INSERT INTO USER_ROLE (user_id, role_id) VALUES (5, 3);


INSERT INTO categories (name, description, deleted, suggested) VALUES
 ('Event Planning', 'Category for organizing event-related tasks',  false, false),
 ('Catering', 'Food and beverages arrangements',   false, false),
 ('Venue Booking', 'Booking venues for events',   true, false),
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


INSERT INTO event_types_suggested_categories VALUES (1, 2),(1, 4),(1,9),(1,7),(2,1),(3,2),(3,4),(3, 7);


INSERT INTO products (id, name, description, specialties, price, discount, status, is_available, is_deleted, is_visible, category_id, provider_id) VALUES
(nextval('solution_sequence'), 'Custom Invitations', 'Beautifully designed customizable invitations for all events', 'Invitations, Customizable', 2.50, 50.00, 'ACCEPTED', TRUE, FALSE, TRUE, 9, 3),
(nextval('solution_sequence'), 'Event Banner', 'High-quality banners for event promotion', 'Banners, Event', 50.00, 10.00, 'ACCEPTED', TRUE, FALSE, TRUE, 10, 3),
(nextval('solution_sequence'), 'Party Favors', 'Unique and personalized party favors for any occasion', 'Party, Favors', 1.50, 20.00, 'ACCEPTED', TRUE, FALSE, TRUE, 1, 3),
(nextval('solution_sequence'), 'Decorative Balloons', 'Colorful balloons for all events', 'Balloons, Decorative', 0.80, 10.00, 'ACCEPTED', TRUE, FALSE, TRUE, 7, 3),
(nextval('solution_sequence'), 'Event T-Shirts', 'Customizable t-shirts for event attendees', 'T-Shirts, Customizable', 15.00, 5.00, 'ACCEPTED', TRUE, FALSE, TRUE, 9, 3),
(nextval('solution_sequence'), 'Party Hats', 'Fun and colorful hats for parties and events', 'Party, Hats', 2.00, 50.0, 'ACCEPTED', TRUE, FALSE, TRUE, 7, 4),
(nextval('solution_sequence'), 'Event Mugs', 'Personalized mugs for event souvenirs', 'Mugs, Personalized', 5.00, 20.00, 'ACCEPTED', TRUE, FALSE, TRUE, 9, 4),
(nextval('solution_sequence'), 'Photo Frames', 'Customizable photo frames for event photos', 'Frames, Customizable', 8.00, 15.00, 'ACCEPTED', TRUE, FALSE, TRUE, 9, 5);


INSERT INTO services (id, name, description, specialties, price, discount, status, is_available, is_deleted, is_visible, type, reservation_deadline, cancellation_deadline, min_duration, max_duration, category_id, provider_id) VALUES
(nextval('solution_sequence'), 'Event Photography', 'Professional photography services for all types of events', 'Photography, Event', 150.00, 30.00, 'ACCEPTED', TRUE, FALSE, TRUE, 'MANUAL', NOW() + INTERVAL '1 day' * (RANDOM() * 30), NOW() + INTERVAL '1 day' * (RANDOM() * 30), 2, 6, 4, 3),
(nextval('solution_sequence'), 'Catering Service', 'Delicious and customizable catering for events', 'Catering, Customizable', 500.00, 50.00, 'ACCEPTED', TRUE, FALSE, TRUE, 'MANUAL', NOW() + INTERVAL '1 day' * (RANDOM() * 30), NOW() + INTERVAL '1 day' * (RANDOM() * 30), 3, 8, 2, 3),
(nextval('solution_sequence'), 'Event Planning', 'Comprehensive event planning services from start to finish', 'Event Planning, Full Service', 1200.00, 0.00, 'ACCEPTED', TRUE, FALSE, TRUE, 'MANUAL', NOW() + INTERVAL '1 day' * (RANDOM() * 30), NOW() + INTERVAL '1 day' * (RANDOM() * 30), 4, 10, 1, 3),
(nextval('solution_sequence'), 'Sound System Setup', 'High-quality sound system rental and setup for events', 'Sound System, Setup', 250.00, 40.00, 'ACCEPTED', TRUE, FALSE, TRUE, 'MANUAL', NOW() + INTERVAL '1 day' * (RANDOM() * 30), NOW() + INTERVAL '1 day' * (RANDOM() * 30), 1, 5, 5, 3),
(nextval('solution_sequence'), 'Decorative Lighting', 'Stunning lighting setups for all events', 'Lighting, Decorative', 300.00, 50.00, 'ACCEPTED', TRUE, FALSE, TRUE, 'MANUAL', NOW() + INTERVAL '1 day' * (RANDOM() * 30), NOW() + INTERVAL '1 day' * (RANDOM() * 30), 2, 4, 7, 3),
(nextval('solution_sequence'), 'Venue Booking', 'Booking service for event venues', 'Venue, Booking', 1000.00, 100.00, 'ACCEPTED', TRUE, FALSE, TRUE, 'MANUAL', NOW() + INTERVAL '1 day' * (RANDOM() * 30), NOW() + INTERVAL '1 day' * (RANDOM() * 30), 6, 12, 3, 3),
(nextval('solution_sequence'), 'Transportation Service', 'Event transportation services for guests and equipment', 'Transportation, Event', 350.00, 60.00, 'PENDING', TRUE, FALSE, TRUE, 'MANUAL', NOW() + INTERVAL '1 day' * (RANDOM() * 30), NOW() + INTERVAL '1 day' * (RANDOM() * 30), 3, 7, 8, 3);


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


INSERT INTO events (name, description, date, privacy, max_participants, type_id, address, city_id, organizer_id, is_draft)
VALUES
('Wedding in Novi Sad', 'A beautiful wedding ceremony with reception and dance.', CURRENT_DATE + INTERVAL '3' DAY, 'OPEN', 100, 1, '123 Wedding St', 2, 2, false),
('Corporate Event in Novi Sad', 'A corporate networking event with speakers and workshops.', CURRENT_DATE + INTERVAL '4' DAY, 'OPEN', 50, 2, '456 Business Ave', 2, 5, false),
('Birthday Bash in Sombor', 'A fun-filled birthday party with music and food.', CURRENT_DATE + INTERVAL '5' DAY, 'OPEN', 30, 3, '789 Birthday Blvd', 6, 4, false),
('Sombor Business Meetup', 'A professional business networking event in Sombor.', CURRENT_DATE + INTERVAL '3' DAY, 'OPEN', 40, 2, '234 Business Rd', 6, 2, false),
('Wedding Reception in Novi Sad', 'An elegant wedding reception with dinner and music.', CURRENT_DATE + INTERVAL '4' DAY, 'OPEN', 80, 1, '321 Reception St', 2, 2, false),
('Birthday Celebration in Beograd', 'A lively birthday party with a band and dancing.', CURRENT_DATE + INTERVAL '5' DAY, 'OPEN', 150, 3, '101 Celebration Ave', 1, 4, false),
('Corporate Seminar in Novi Sad', 'A corporate seminar about leadership and growth.', CURRENT_DATE + INTERVAL '3' DAY, 'OPEN', 70, 2, '999 Conference Blvd', 2, 5, false),
('Trebinje Wedding Ceremony', 'A traditional wedding ceremony in Trebinje.', CURRENT_DATE + INTERVAL '4' DAY, 'OPEN', 120, 1, '555 Wedding Plaza', 3, 2, false),
('Kraljevo Birthday Party', 'A birthday party with a surprise guest performance.', CURRENT_DATE + INTERVAL '3' DAY, 'OPEN', 50, 3, '888 Party St', 5, 4, false),
('Corporate Launch in Sremska Mitrovica', 'A corporate product launch event with media coverage.', CURRENT_DATE + INTERVAL '4' DAY, 'OPEN', 200, 2, '777 Launch Ave', 4, 5, false),
('Team Building Event in Kragujevac', 'An outdoor team building event with games and activities.', CURRENT_DATE + INTERVAL '5' DAY, 'OPEN', 60, 2, '123 Team Rd', 7, 2, false),
('Wedding Gala in Novi Sad', 'An extravagant wedding gala with special guests and entertainment.', CURRENT_DATE + INTERVAL '3' DAY, 'OPEN', 200, 1, '444 Gala St', 2, 2, false),
('Sombor Conference', 'A professional conference on innovation and technology in Sombor.', CURRENT_DATE + INTERVAL '4' DAY, 'OPEN', 100, 2, '222 Conference St', 6, 5, false),
('Birthday Extravaganza in Novi Sad', 'A large birthday party with multiple DJs and live performances.', CURRENT_DATE + INTERVAL '5' DAY, 'OPEN', 300, 3, '123 Party Plaza', 2, 4, false),
('Beograd Business Summit', 'A high-level business summit for industry leaders.', CURRENT_DATE + INTERVAL '3' DAY, 'OPEN', 250, 2, '555 Summit Rd', 1, 5, false),
('Kraljevo Music Festival', 'A large outdoor music festival with multiple stages and bands.', CURRENT_DATE + INTERVAL '4' DAY, 'OPEN', 1000, 3, '678 Festival Blvd', 5, 4, false),
('Wedding Expo in Novi Sad', 'A wedding exhibition with bridal gowns, cakes, and services.', CURRENT_DATE + INTERVAL '3' DAY, 'OPEN', 300, 1, '333 Expo Ave', 2, 2, false),
('Sombor Annual Fair', 'An annual fair showcasing local businesses and crafts.', CURRENT_DATE + INTERVAL '4' DAY, 'OPEN', 500, 2, '123 Fair Rd', 6, 4, false),
('Corporate Retreat in Trebinje', 'A corporate retreat for team bonding and relaxation.', CURRENT_DATE + INTERVAL '5' DAY, 'OPEN', 50, 2, '234 Retreat Ave', 3, 5, false),
('Birthday Fest in Beograd', 'A multi-location birthday festival with food trucks, music, and games.', CURRENT_DATE + INTERVAL '3' DAY, 'OPEN', 200, 3, '111 Fest Rd', 1, 4, false),
('Sombor Cultural Night', 'A cultural night celebrating local artists, music, and food.', CURRENT_DATE + INTERVAL '4' DAY, 'OPEN', 100, 3, '444 Culture St', 6, 2, false);


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


INSERT INTO solution_event_types (solution_id, event_type_id) VALUES
(1, 1), (1, 2), (1, 3), (2, 1), (2, 2), (3, 3), (4, 1), (4, 2), (5, 1), (5, 2),
(6, 3), (7, 1), (7, 2), (8, 3), (9, 1), (10, 2), (11, 3), (12, 1), (13, 2),
(14, 3), (15, 1);


INSERT INTO images (content_type, path) VALUES
('image/jpeg', 'custom_invitations.jpg'),
('image/jpeg', 'event_banner.jpg'),
('image/jpeg', 'party_favors.jpg'),
('image/jpeg', 'decorative_balloons.jpg'),
('image/jpeg', 'event_t-shirts.jpg'),
('image/jpeg', 'party_hats.jpg'),
('image/jpeg', 'event_mugs.jpg'),
('image/jpeg', 'photo_frames.jpg'),
('image/jpeg', 'event_photography.jpg'),
('image/jpeg', 'catering_service.jpg'),
('image/jpeg', 'event_planning.jpg'),
('image/png', 'sound_system_setup.png'),
('image/jpeg', 'decorative_lighting.jpg'),
('image/jpeg', 'venue_booking.jpg'),
('image/jpeg', 'transportation_service.jpg');


INSERT INTO solutions_history VALUES (1);


INSERT INTO solution_image_paths (image_paths_id, solution_id) VALUES
(1, 1), (2, 2), (3, 3),
(4, 4), (5, 5), (6, 6),
(7, 7), (8, 8), (9, 9),
(10, 10), (11, 11), (12, 12),
(13, 13), (14, 14), (15, 15);
