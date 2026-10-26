-- Demo seed data for the supplied 16-table ERD (inserted only during first successful bootstrap).
-- Amounts are stored as cents, not euros.

INSERT INTO student (id, first_name, last_name, address, birth_date, phone, email, level) VALUES
('S1', 'An', 'Nguyen', 'Paris', '2008-04-12', '0600000001', 'an@example.com', 'BEGINNER'),
('S2', 'Binh', 'Tran', 'Chelles', '2007-11-20', '0600000002', 'binh@example.com', 'INTERMEDIATE');

INSERT INTO teacher (id, first_name, last_name, hourly_rate_cents, qualifications) VALUES
('T1', 'Minh', 'Le', 3000, 'Piano diploma'),
('T2', 'Emma', 'Martin', 3500, 'Violin diploma');

INSERT INTO teacher_specialization (teacher_id, instrument_type) VALUES
('T1', 'PIANO'),
('T2', 'VIOLIN');

INSERT INTO student_preference (student_id, instrument_type) VALUES
('S1', 'PIANO'),
('S2', 'VIOLIN');

INSERT INTO room (id, name, capacity, hourly_price_cents) VALUES
('R1', 'Piano Room', 5, 1500),
('R2', 'Rehearsal Room', 12, 2200);

INSERT INTO instrument (id, name, instrument_type, hourly_price_cents) VALUES
('I1', 'Grand Piano', 'PIANO', 1200),
('I2', 'Violin', 'VIOLIN', 900);

INSERT INTO package_offer (id, name, price_cents, unlimited, lesson_count) VALUES
('P5', '5 lessons', 18000, FALSE, 5),
('PU', 'Unlimited', 60000, TRUE, NULL);
