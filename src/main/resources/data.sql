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

INSERT INTO event_type (name, description, deleted) VALUES
('Wedding', 'Event type for organizing weddings', false),
('Corporate Event', 'Event type for organizing corporate events', false),
('Birthday Party', 'Event type for organizing birthdays', false);

INSERT INTO event_type_suggested_categories VALUES (1, 2),(1, 4),(1,9),(1,7),(2,1),(3,2),(3,4),(3, 7);