INSERT INTO category (name, description, status, deleted) VALUES
 ('Event Planning', 'Category for organizing event-related tasks', 'ACCEPTED',  false),
 ('Catering', 'Food and beverages arrangements', 'PENDING',  false),
 ('Venue Booking', 'Booking venues for events', 'DECLINED',  true),
 ('Photography', 'Photography and videography services', 'ACCEPTED',  false),
 ('Entertainment', 'Music, shows, and performances', 'PENDING',  false),
 ('Logistics', 'Transport and materials management', 'ACCEPTED',  false),
 ('Decoration', 'Venue decoration and themes', 'DECLINED',  true),
 ('Security', 'Security and crowd control', 'ACCEPTED',  false),
 ('Guest Management', 'Handling guest invitations and RSVP', 'PENDING',  false),
 ('Marketing', 'Promotions and event advertising', 'ACCEPTED',  false);

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
