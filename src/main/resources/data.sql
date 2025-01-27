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
                                            ('image/jpeg', 'transportation_service.jpg'),
                                            ('image/jpg', 'photo1.jpg'),
                                            ('image/jpg', 'photo2.jpg'),
                                            ('image/png', 'logo.png'), --18
                                            ('image/png', 'in_company.png'), --19
                                            ('image/png', 'event_planned_by_us.png'), --20
                                            ('image/png', 'logo.png'), --21
                                            ('image/png', 'bday_decoration.png'), --22
                                            ('image/png', 'decorated_by_us.png'); --23


INSERT INTO users (activated, city_id, suspended, activation_timestamp, address, email, lastname, name, password, phone_number, last_password_reset, hash, profile_photo_id) VALUES
    (true, 6,  null, '2024-12-07 12:00:00', 'Staparski put 18', 'organizer@gmail.com', 'Doe', 'John', '$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W', '1234567890', '2017-10-01 21:58:58.508-07', '1', 16),
    (true, 2,  null, '2024-12-06 12:00:00', 'Bulevar oslobodjenja, 20', 'jane.smith@example.com', 'Smith', 'Jane', '$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W', '9876543210', '2017-10-01 21:58:58.508-07', '2', null),
    (true, 2,  null, '2024-12-06 12:00:00', 'Mise Dimitrijevica, 7', 'provider@gmail.com', 'Johnson', 'Emily', '$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W', '5551234567', '2017-10-01 21:58:58.508-07', '3', null),
    (true, 6,  null, '2024-12-05 12:00:00', 'Venac Radomira Putnika 5', 'michael.brown@example.com', 'Brown', 'Michael', '$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W', '1112223333', '2017-10-01 21:58:58.508-07', '4', null),
    (true, 1,  null, '2024-12-06 12:00:00', 'Njegoševa 12', 'admin@gmail.com', 'Davis', 'Sarah', '$2a$10$Z3JiBldbaNQ4qGPjtr7TV.FeT2He/KgqxT68impZ9.H3XeyQAZ03W', '4445556666', '2017-10-01 21:58:58.508-07', '5', 17);


INSERT INTO USER_ROLE (user_id, role_id) VALUES (1, 5);
INSERT INTO USER_ROLE (user_id, role_id) VALUES (2, 5);
INSERT INTO USER_ROLE (user_id, role_id) VALUES (3, 4);
INSERT INTO USER_ROLE (user_id, role_id) VALUES (4, 4);
INSERT INTO USER_ROLE (user_id, role_id) VALUES (5, 3);

INSERT INTO companies (city_id, closing_hours, opening_hours, address, description, email, name, phone_number, provider_id)
VALUES
    (3, '9:00 pm', '9:00 am', 'Dositejeva, 6',
     'Event Masters is your go-to partner for planning all types of events. ' ||
     'From creating custom invitations and event banners to full-scale coordination, ' ||
     'we bring your vision to life with precision and creativity. Contact us to make your event unforgettable.',
     'info@eventmasters.com',
     'Event Masters', '+15551234567', 3),
    (4, '5:00 pm', '7:00 am', 'Nemanjina, 15',
     'Celebrate in style with Party Supplies Hub! Offering a wide range of balloons, party decorations, sound systems, and catering options. ' ||
     'We are your one-stop shop for making every celebration unforgettable.',
     'support@partysupplies.com',
     'Party Supplies Hub', '+15554567890', 4);

insert into companies_photos (company_id, photos_id) values
 (1, 18),
 (1, 19),
 (1, 20),
 (2, 21),
 (2, 22),
 (2, 23);
    

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


INSERT INTO event_types_suggested_categories VALUES (1, 2),(1, 4),(1,9),(1,7),(2,1),(3,2),(3,4),(3, 7);


INSERT INTO products (id, name, description, price, discount, status, is_available, is_deleted, is_visible, category_id, provider_id) VALUES
(nextval('solution_sequence'), 'Custom Invitations', 'Beautifully designed customizable invitations for all events', 2.50, 50.00, 'ACCEPTED', TRUE, FALSE, TRUE, 9, 3),
(nextval('solution_sequence'), 'Event Banner', 'High-quality banners for event promotion', 50.00, 10.00, 'ACCEPTED', TRUE, FALSE, TRUE, 10, 3),
(nextval('solution_sequence'), 'Party Favors', 'Unique and personalized party favors for any occasion', 1.50, 20.00, 'ACCEPTED', TRUE, FALSE, TRUE, 1, 3),
(nextval('solution_sequence'), 'Decorative Balloons', 'Colorful balloons for all events', 0.80, 10.00, 'ACCEPTED', TRUE, FALSE, TRUE, 7, 3),
(nextval('solution_sequence'), 'Event T-Shirts', 'Customizable t-shirts for event attendees', 15.00, 5.00, 'ACCEPTED', TRUE, FALSE, TRUE, 9, 3),
(nextval('solution_sequence'), 'Party Hats', 'Fun and colorful hats for parties and events', 2.00, 50.0, 'ACCEPTED', TRUE, FALSE, TRUE, 7, 4),
(nextval('solution_sequence'), 'Event Mugs', 'Personalized mugs for event souvenirs', 5.00, 20.00, 'ACCEPTED', TRUE, FALSE, TRUE, 9, 4),
(nextval('solution_sequence'), 'Photo Frames', 'Customizable photo frames for event photos', 8.00, 15.00, 'ACCEPTED', TRUE, FALSE, TRUE, 9, 3);


INSERT INTO services (id, name, description, specialties, price, discount, status, is_available, is_deleted, is_visible, type, reservation_deadline, cancellation_deadline, min_duration, max_duration, category_id, provider_id) VALUES
(nextval('solution_sequence'), 'Event Photography', 'Professional photography services for all types of events', 'Photography, Event', 150.00, 30.00, 'ACCEPTED', TRUE, FALSE, TRUE, 'MANUAL', 14, 3, 2, 6, 4, 3),
(nextval('solution_sequence'), 'Catering Service', 'Delicious and customizable catering for events', 'Catering, Customizable', 500.00, 50.00, 'ACCEPTED', TRUE, FALSE, TRUE, 'MANUAL', 21, 5, 3, 8, 2, 3),
(nextval('solution_sequence'), 'Event Planning', 'Comprehensive event planning services from start to finish', 'Event Planning, Full Service', 1200.00, 0.00, 'ACCEPTED', TRUE, FALSE, TRUE, 'MANUAL', 30, 10, 4, 10, 1, 3),
(nextval('solution_sequence'), 'Sound System Setup', 'High-quality sound system rental and setup for events', 'Sound System, Setup', 250.00, 40.00, 'ACCEPTED', TRUE, FALSE, TRUE, 'MANUAL', 7, 2, 1, 5, 5, 3),
(nextval('solution_sequence'), 'Decorative Lighting', 'Stunning lighting setups for all events', 'Lighting, Decorative', 300.00, 50.00, 'ACCEPTED', TRUE, FALSE, TRUE, 'MANUAL', 14, 4, 2, 4, 7, 3),
(nextval('solution_sequence'), 'Venue Booking', 'Booking service for event venues', 'Venue, Booking', 1000.00, 100.00, 'ACCEPTED', TRUE, FALSE, TRUE, 'MANUAL', 21, 7, 6, 12, 3, 3),
(nextval('solution_sequence'), 'Transportation Service', 'Event transportation services for guests and equipment', 'Transportation, Event', 350.00, 60.00, 'PENDING', TRUE, FALSE, TRUE, 'MANUAL', 14, 5, 3, 7, 8, 3);


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
('Wedding in Novi Sad', 'A beautiful wedding ceremony with reception and dance.', CURRENT_DATE + INTERVAL '3' DAY, 'OPEN', 100, 1, '123 Wedding St', 2, 1, false),
('Corporate Event in Novi Sad', 'A corporate networking event with speakers and workshops.', CURRENT_DATE + INTERVAL '4' DAY, 'OPEN', 50, 2, '456 Business Ave', 2, 2, false),
('Birthday Bash in Sombor', 'A fun-filled birthday party with music and food.', CURRENT_DATE + INTERVAL '5' DAY, 'OPEN', 30, 3, '789 Birthday Blvd', 6, 1, false),
('Sombor Business Meetup', 'A professional business networking event in Sombor.', CURRENT_DATE + INTERVAL '3' DAY, 'OPEN', 40, 2, '234 Business Rd', 6, 2, false),
('Wedding Reception in Novi Sad', 'An elegant wedding reception with dinner and music.', CURRENT_DATE + INTERVAL '4' DAY, 'OPEN', 80, 1, '321 Reception St', 2, 2, false),
('Birthday Celebration in Beograd', 'A lively birthday party with a band and dancing.', CURRENT_DATE + INTERVAL '5' DAY, 'OPEN', 150, 3, '101 Celebration Ave', 1, 2, false),
('Corporate Seminar in Novi Sad', 'A corporate seminar about leadership and growth.', CURRENT_DATE + INTERVAL '3' DAY, 'OPEN', 70, 2, '999 Conference Blvd', 2, 1, false),
('Trebinje Wedding Ceremony', 'A traditional wedding ceremony in Trebinje.', CURRENT_DATE + INTERVAL '4' DAY, 'OPEN', 120, 1, '555 Wedding Plaza', 3, 2, false),
('Kraljevo Birthday Party', 'A birthday party with a surprise guest performance.', CURRENT_DATE + INTERVAL '3' DAY, 'OPEN', 50, 3, '888 Party St', 5, 1, false),
('Corporate Launch in Sremska Mitrovica', 'A corporate product launch event with media coverage.', CURRENT_DATE + INTERVAL '4' DAY, 'OPEN', 200, 2, '777 Launch Ave', 4, 1, false),
('Team Building Event in Kragujevac', 'An outdoor team building event with games and activities.', CURRENT_DATE + INTERVAL '5' DAY, 'OPEN', 60, 2, '123 Team Rd', 7, 2, false),
('Wedding Gala in Novi Sad', 'An extravagant wedding gala with special guests and entertainment.', CURRENT_DATE + INTERVAL '3' DAY, 'OPEN', 200, 1, '444 Gala St', 2, 2, false),
('Sombor Conference', 'A professional conference on innovation and technology in Sombor.', CURRENT_DATE + INTERVAL '4' DAY, 'OPEN', 100, 2, '222 Conference St', 6, 1, false),
('Birthday Extravaganza in Novi Sad', 'A large birthday party with multiple DJs and live performances.', CURRENT_DATE + INTERVAL '5' DAY, 'OPEN', 300, 3, '123 Party Plaza', 2, 1, false),
('Beograd Business Summit', 'A high-level business summit for industry leaders.', CURRENT_DATE + INTERVAL '3' DAY, 'OPEN', 250, 2, '555 Summit Rd', 1, 2, false),
('Kraljevo Music Festival', 'A large outdoor music festival with multiple stages and bands.', CURRENT_DATE + INTERVAL '4' DAY, 'OPEN', 1000, 3, '678 Festival Blvd', 5, 1, false),
('Wedding Expo in Novi Sad', 'A wedding exhibition with bridal gowns, cakes, and services.', CURRENT_DATE + INTERVAL '3' DAY, 'OPEN', 300, 1, '333 Expo Ave', 2, 2, false),
('Sombor Annual Fair', 'An annual fair showcasing local businesses and crafts.', CURRENT_DATE + INTERVAL '4' DAY, 'OPEN', 500, 2, '123 Fair Rd', 6, 4, false),
('Corporate Retreat in Trebinje', 'A corporate retreat for team bonding and relaxation.', CURRENT_DATE + INTERVAL '5' DAY, 'OPEN', 50, 2, '234 Retreat Ave', 3, 1, false),
('Birthday Fest in Beograd', 'A multi-location birthday festival with food trucks, music, and games.', CURRENT_DATE + INTERVAL '3' DAY, 'OPEN', 200, 3, '111 Fest Rd', 1, 1, false),
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


INSERT INTO solutions_history VALUES (1);


INSERT INTO solution_image_paths (image_paths_id, solution_id) VALUES
(1, 1), (2, 2), (3, 3),
(4, 4), (5, 5), (6, 6),
(7, 7), (8, 8), (9, 9),
(10, 10), (11, 11), (12, 12),
(13, 13), (14, 14), (15, 15);

INSERT INTO reports (reason, timestamp, offender_id, reporter_id, status) VALUES
    ( 'Inappropriate behavior', '2025-01-12T10:15:30', 1, 2, 'PENDING'),
    ('Spam content', '2025-01-11T14:20:00', 3, 4, 'PENDING'),
    ('Violation of privacy', '2025-01-10T18:45:00', 1, 3, 'PENDING'),
    ('Abusive language', '2025-01-09T09:00:00', 2, 5, 'PENDING'),
    ('Fake account creation', '2025-01-08T16:30:00', 4, 5, 'PENDING'),
    ('Offensive content in posts', '2025-01-07T12:10:00', 4, 1, 'PENDING'),
    ('Threatening behavior', '2025-01-06T14:50:00', 2, 3, 'PENDING'),
    ('Hate speech', '2025-01-05T17:25:00', 3, 2, 'PENDING'),
    ('Impersonation of another user', '2025-01-04T08:15:00', 4, 1, 'PENDING'),
    ('Scamming attempt', '2025-01-03T19:05:00', 5, 1, 'PENDING'),
    ('Malicious links in messages', '2025-01-02T22:40:00', 1, 2, 'PENDING'),
    ('Inappropriate content in profile', '2025-01-01T10:30:00', 1, 3, 'PENDING'),
    ('Sharing unauthorized materials', '2024-12-31T16:55:00', 2, 4, 'PENDING'),
    ('Manipulating users', '2024-12-30T20:25:00', 3, 5, 'PENDING'),
    ('Posting illegal content', '2024-12-29T18:10:00', 4, 1, 'PENDING'),
    ('Soliciting personal information', '2024-12-28T21:00:00', 5, 3, 'PENDING'),
    ('Misleading advertisements', '2024-12-27T15:50:00', 1, 5, 'PENDING'),
    ('Spreading misinformation', '2024-12-26T11:35:00', 4, 3, 'PENDING'),
    ('Disruptive behavior in public discussions', '2024-12-25T09:10:00', 5, 2, 'PENDING'),
    ('Violation of terms of service', '2024-12-24T14:00:00', 1, 3, 'PENDING');

INSERT INTO notifications (seen, id, recipient_id, timestamp, message, title) VALUES
    (true, 1, null, '2025-01-26 16:41:51.040058', 'A new category proposal (Audio/Visual Equipment) has been created!', 'Category'),
    (true, 2, null, '2025-01-26 16:42:28.426459', 'A new category proposal (Audio/Visual Equipment) has been created!', 'Category'),
    (true, 3, null, '2025-01-26 16:43:05.987654', 'A new category proposal (Catering Equipment) has been created!', 'Category'),
    (true, 4, null, '2025-01-26 16:44:12.123456', 'A new category proposal (Event Furniture) has been created!', 'Category'),
    (false, 5, null, '2025-01-26 16:45:45.789123', 'A new category proposal (Lighting and Effects) has been created!', 'Category'),
    (false, 6, null, '2025-01-26 16:46:59.654321', 'A new category proposal (Entertainment and Activities) has been created!', 'Category'),
    (false, 7, null, '2025-01-26 16:47:33.999999', 'A new category proposal (Event Staffing and Security) has been created!', 'Category'),
    (false, 8, null, '2025-01-28 00:24:06.023342', 'A new category proposal (Musical Instruments) has been created!', 'Category'),
    (true, 9, 3, '2025-01-28 00:26:10.042144', 'Your category suggestion (Musical Instruments) has been accepted.', 'Category'),
    (true, 10, 4, '2025-01-28 01:00:10.042144', 'Your category suggestion (Audio/Visual Equipment) has been accepted.', 'Category'),
    (false, 11, null, '2025-01-28 01:10:15.112233', 'A new category proposal (Sports Equipment) has been created!', 'Category'),
    (false, 12, null, '2025-01-28 01:20:25.223344', 'A new category proposal (Office Supplies) has been created!', 'Category'),
    (true, 13, 3, '2025-01-28 01:30:30.334455', 'Your category suggestion (Catering Equipment) has been accepted.', 'Category');