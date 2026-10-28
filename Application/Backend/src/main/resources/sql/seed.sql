INSERT INTO student (id, first_name, last_name, address, birth_date, phone, email, level) VALUES
    ('STU_001', 'An', 'Nguyen', '18 Rue des Ecoles, 75005 Paris', '2006-01-12', '0614827365', 'an.nguyen@example.com', 'BEGINNER'),
    ('STU_002', 'Binh', 'Tran', '42 Avenue de la Republique, 75011 Paris', '2007-02-12', '0673951824', 'binh.tran@example.com', 'INTERMEDIATE'),
    ('STU_003', 'Chi', 'Le', '7 Rue de la Gare, 77500 Chelles', '2008-03-12', '0628465917', 'chi.le@example.com', 'ADVANCED'),
    ('STU_004', 'Dung', 'Pham', '23 Rue Victor Hugo, 93160 Noisy-le-Grand', '2009-04-12', '0751632849', 'dung.pham@example.com', 'BEGINNER'),
    ('STU_005', 'Giang', 'Hoang', '11 Avenue Jean Jaures, 75019 Paris', '2005-05-12', '0649273186', 'giang.hoang@example.com', 'INTERMEDIATE'),
    ('STU_006', 'Ha', 'Vu', '36 Rue du Moulin, 77420 Champs-sur-Marne', '2006-06-12', '0762841935', 'ha.vu@example.com', 'ADVANCED'),
    ('STU_007', 'Khanh', 'Dang', '9 Rue de Paris, 94300 Vincennes', '2007-07-12', '0637518294', 'khanh.dang@example.com', 'BEGINNER'),
    ('STU_008', 'Linh', 'Do', '15 Avenue du General de Gaulle, 94170 Le Perreux-sur-Marne', '2008-08-12', '0784125639', 'linh.do@example.com', 'INTERMEDIATE'),
    ('STU_009', 'Minh', 'Bui', '28 Rue Pasteur, 93360 Neuilly-Plaisance', '2009-09-12', '0659382417', 'minh.bui@example.com', 'ADVANCED'),
    ('STU_010', 'Nam', 'Ngo', '6 Rue des Lilas, 94130 Nogent-sur-Marne', '2005-10-12', '0719463825', 'nam.ngo@example.com', 'BEGINNER');

INSERT INTO teacher (id, first_name, last_name, hourly_rate_cents, qualifications) VALUES
    ('TCH_001', 'Emma', 'Martin', 2900, 'Piano teaching diploma'),
    ('TCH_002', 'Lucas', 'Bernard', 3000, 'Guitar teaching diploma'),
    ('TCH_003', 'Chloe', 'Dubois', 3100, 'Violin teaching diploma'),
    ('TCH_004', 'Louis', 'Thomas', 3200, 'Flute teaching diploma'),
    ('TCH_005', 'Jade', 'Robert', 3300, 'Drums teaching diploma'),
    ('TCH_006', 'Hugo', 'Richard', 3400, 'Piano teaching diploma'),
    ('TCH_007', 'Alice', 'Petit', 3500, 'Guitar teaching diploma'),
    ('TCH_008', 'Leo', 'Durand', 3600, 'Violin teaching diploma'),
    ('TCH_009', 'Louise', 'Moreau', 3700, 'Flute teaching diploma'),
    ('TCH_010', 'Gabriel', 'Laurent', 3800, 'Drums teaching diploma');

INSERT INTO student_preference (student_id, instrument_type) VALUES
    ('STU_001', 'PIANO'),
    ('STU_002', 'GUITAR'),
    ('STU_003', 'VIOLIN'),
    ('STU_004', 'FLUTE'),
    ('STU_005', 'DRUMS'),
    ('STU_006', 'PIANO'),
    ('STU_007', 'GUITAR'),
    ('STU_008', 'VIOLIN'),
    ('STU_009', 'FLUTE'),
    ('STU_010', 'DRUMS');

INSERT INTO teacher_specialization (teacher_id, instrument_type) VALUES
    ('TCH_001', 'PIANO'),
    ('TCH_002', 'GUITAR'),
    ('TCH_003', 'VIOLIN'),
    ('TCH_004', 'FLUTE'),
    ('TCH_005', 'DRUMS'),
    ('TCH_006', 'PIANO'),
    ('TCH_007', 'GUITAR'),
    ('TCH_008', 'VIOLIN'),
    ('TCH_009', 'FLUTE'),
    ('TCH_010', 'DRUMS');

INSERT INTO teacher_availability (teacher_id, slot_number, starts_at, ends_at) VALUES
    ('TCH_001', 1, 1791612000, 1791648000),
    ('TCH_002', 1, 1793775600, 1793811600),
    ('TCH_003', 1, 1792476000, 1792512000),
    ('TCH_004', 1, 1793948400, 1793984400),
    ('TCH_005', 1, 1794034800, 1794070800),
    ('TCH_006', 1, 1794121200, 1794157200),
    ('TCH_007', 1, 1794207600, 1794243600),
    ('TCH_008', 1, 1794294000, 1794330000),
    ('TCH_009', 1, 1794380400, 1794416400),
    ('TCH_010', 1, 1794466800, 1794502800);

INSERT INTO room (id, name, capacity, hourly_price_cents) VALUES
    ('RM_PIA_01', 'Piano Studio A', 5, 1500),
    ('RM_GT_01', 'Guitar Studio A', 8, 1600),
    ('RM_VIO_01', 'Violin Studio A', 6, 1700),
    ('RM_FL_01', 'Flute Studio A', 4, 1800),
    ('RM_ENSEMBLE_01', 'Ensemble Hall A', 10, 1900),
    ('RM_ENSEMBLE_02', 'Ensemble Hall B', 8, 2000),
    ('RM_GT_02', 'Guitar Studio B', 3, 2100),
    ('RM_FL_02', 'Flute Studio B', 12, 2200),
    ('RM_REHEARSAL_01', 'Rehearsal Studio', 6, 2300),
    ('RM_PERC_01', 'Percussion Studio', 5, 2400);

INSERT INTO instrument (id, name, instrument_type, hourly_price_cents) VALUES
    ('PIA_01', 'Concert Grand Piano', 'PIANO', 1200),
    ('GT_01', 'Classical Guitar', 'GUITAR', 700),
    ('VIO_01', 'Student Violin', 'VIOLIN', 900),
    ('FL_01', 'Silver Flute', 'FLUTE', 600),
    ('DRM_01', 'Acoustic Drum Kit', 'DRUMS', 1000),
    ('PIA_02', 'Upright Piano', 'PIANO', 1300),
    ('GT_02', 'Acoustic Steel-String Guitar', 'GUITAR', 800),
    ('VIO_02', 'Advanced Violin', 'VIOLIN', 950),
    ('FL_02', 'Student Flute', 'FLUTE', 650),
    ('DRM_02', 'Electronic Drum Kit', 'DRUMS', 1100);

INSERT INTO package_offer (id, name, price_cents, unlimited, lesson_count) VALUES
    ('PKG_START_05', 'Starter 5 lessons', 18000, FALSE, 5),
    ('PKG_STD_10', 'Standard 10 lessons', 34000, FALSE, 10),
    ('PKG_PROG_15', 'Progress 15 lessons', 48000, FALSE, 15),
    ('PKG_ADV_20', 'Advanced 20 lessons', 60000, FALSE, 20),
    ('PKG_PRACT_08', 'Practice 8 lessons', 28000, FALSE, 8),
    ('PKG_INTRO_03', 'Discovery 3 lessons', 12000, FALSE, 3),
    ('PKG_INTENS_12', 'Intensive 12 lessons', 40000, FALSE, 12),
    ('PKG_UNL_BASIC', 'Unlimited Basic', 60000, TRUE, NULL),
    ('PKG_UNL_PLUS', 'Unlimited Plus', 75000, TRUE, NULL),
    ('PKG_UNL_PREM', 'Unlimited Premium', 90000, TRUE, NULL);

INSERT INTO enrollment (id, student_id, package_id, valid_from, valid_through, purchased_at, package_name, price_cents, unlimited, lesson_count) VALUES
    ('ENR_001', 'STU_001', 'PKG_START_05', '2026-10-01', '2026-12-31', 1790838000, 'Starter 5 lessons', 18000, FALSE, 5),
    ('ENR_002', 'STU_002', 'PKG_STD_10', '2026-10-01', '2026-12-31', 1790838000, 'Standard 10 lessons', 34000, FALSE, 10),
    ('ENR_003', 'STU_003', 'PKG_PROG_15', '2026-10-01', '2026-12-31', 1790838000, 'Progress 15 lessons', 48000, FALSE, 15),
    ('ENR_004', 'STU_004', 'PKG_ADV_20', '2026-10-01', '2026-12-31', 1790838000, 'Advanced 20 lessons', 60000, FALSE, 20),
    ('ENR_005', 'STU_005', 'PKG_PRACT_08', '2026-10-01', '2026-12-31', 1790838000, 'Practice 8 lessons', 28000, FALSE, 8),
    ('ENR_006', 'STU_006', 'PKG_INTRO_03', '2026-10-01', '2026-12-31', 1790838000, 'Discovery 3 lessons', 12000, FALSE, 3),
    ('ENR_007', 'STU_007', 'PKG_INTENS_12', '2026-10-01', '2026-12-31', 1790838000, 'Intensive 12 lessons', 40000, FALSE, 12),
    ('ENR_008', 'STU_008', 'PKG_UNL_BASIC', '2026-10-01', '2026-12-31', 1790838000, 'Unlimited Basic', 60000, TRUE, NULL),
    ('ENR_009', 'STU_009', 'PKG_UNL_PLUS', '2026-10-01', '2026-12-31', 1790838000, 'Unlimited Plus', 75000, TRUE, NULL),
    ('ENR_010', 'STU_010', 'PKG_UNL_PREM', '2026-10-01', '2026-10-20', 1790838000, 'Unlimited Premium', 90000, TRUE, NULL);

INSERT INTO activity (id, kind, starts_at, duration_minutes, room_id, teacher_id, status, changed_at, instrument_type, group_lesson, exam_name, exam_capacity, renter_id, rental_price_cents) VALUES
    ('LES_PIA_01', 'LESSON', 1791619200, 60, 'RM_PIA_01', 'TCH_001', 'COMPLETED', 1791622800, 'PIANO', TRUE, NULL, NULL, NULL, NULL),
    ('LES_GT_01', 'LESSON', 1793782800, 60, 'RM_GT_01', 'TCH_002', 'SCHEDULED', NULL, 'GUITAR', TRUE, NULL, NULL, NULL, NULL),
    ('LES_VIO_01', 'LESSON', 1792483200, 60, 'RM_VIO_01', 'TCH_003', 'CANCELLED', 1792436400, 'VIOLIN', TRUE, NULL, NULL, NULL, NULL),
    ('LES_FL_01', 'LESSON', 1793955600, 60, 'RM_FL_01', 'TCH_004', 'SCHEDULED', NULL, 'FLUTE', FALSE, NULL, NULL, NULL, NULL),
    ('EX_PIA_01', 'EXAM', 1792065600, 60, 'RM_ENSEMBLE_01', NULL, 'COMPLETED', 1792069200, 'PIANO', NULL, 'Piano autumn exam', 5, NULL, NULL),
    ('EX_VIO_01', 'EXAM', 1792152000, 60, 'RM_ENSEMBLE_02', NULL, 'COMPLETED', 1792155600, 'VIOLIN', NULL, 'Violin autumn exam', 5, NULL, NULL),
    ('EX_GT_01', 'EXAM', 1794315600, 60, 'RM_GT_02', NULL, 'SCHEDULED', NULL, 'GUITAR', NULL, 'Classical guitar individual assessment', 1, NULL, NULL),
    ('EX_FL_01', 'EXAM', 1794488400, 90, 'RM_FL_02', NULL, 'SCHEDULED', NULL, 'FLUTE', NULL, 'Flute autumn examination', 10, NULL, NULL),
    ('RENT_ROOM_01', 'ROOM_RENTAL', 1794060000, 120, 'RM_REHEARSAL_01', NULL, 'SCHEDULED', NULL, NULL, NULL, NULL, NULL, 'STU_001', 4600),
    ('RENT_DRM_01', 'INSTRUMENT_RENTAL', 1794146400, 90, NULL, NULL, 'SCHEDULED', NULL, NULL, NULL, NULL, NULL, 'STU_002', 1650);

INSERT INTO activity_instrument (activity_id, instrument_id) VALUES
    ('LES_PIA_01', 'PIA_01'),
    ('LES_PIA_01', 'PIA_02'),
    ('LES_GT_01', 'GT_01'),
    ('LES_VIO_01', 'VIO_01'),
    ('LES_FL_01', 'FL_01'),
    ('EX_PIA_01', 'PIA_01'),
    ('EX_VIO_01', 'VIO_01'),
    ('EX_GT_01', 'GT_01'),
    ('EX_FL_01', 'FL_01'),
    ('RENT_DRM_01', 'DRM_02');

INSERT INTO lesson_participation (lesson_id, student_id, enrollment_id, minutes, state, reserved_at, settled_at) VALUES
    ('LES_PIA_01', 'STU_001', 'ENR_001', 60, 'CONSUMED', 1790838000, 1791622800),
    ('LES_PIA_01', 'STU_002', 'ENR_002', 60, 'CONSUMED', 1790838000, 1791622800),
    ('LES_PIA_01', 'STU_003', 'ENR_003', 60, 'CONSUMED', 1790838000, 1791622800),
    ('LES_GT_01', 'STU_004', 'ENR_004', 60, 'RESERVED', 1790838000, NULL),
    ('LES_GT_01', 'STU_005', 'ENR_005', 60, 'RESERVED', 1790838000, NULL),
    ('LES_GT_01', 'STU_006', 'ENR_006', 60, 'RESERVED', 1790838000, NULL),
    ('LES_VIO_01', 'STU_007', 'ENR_007', 60, 'CONSUMED', 1790838000, 1792436400),
    ('LES_VIO_01', 'STU_008', 'ENR_008', 60, 'CONSUMED', 1790838000, 1792436400),
    ('LES_VIO_01', 'STU_009', 'ENR_009', 60, 'CONSUMED', 1790838000, 1792436400),
    ('LES_FL_01', 'STU_010', NULL, 60, 'RESERVED', 1790838000, NULL);

INSERT INTO exam_registration (exam_id, student_id, registered_at) VALUES
    ('EX_PIA_01', 'STU_001', 1790838000),
    ('EX_PIA_01', 'STU_002', 1790838000),
    ('EX_PIA_01', 'STU_003', 1790838000),
    ('EX_PIA_01', 'STU_004', 1790838000),
    ('EX_PIA_01', 'STU_005', 1790838000),
    ('EX_VIO_01', 'STU_006', 1790838000),
    ('EX_VIO_01', 'STU_007', 1790838000),
    ('EX_VIO_01', 'STU_008', 1790838000),
    ('EX_VIO_01', 'STU_009', 1790838000),
    ('EX_VIO_01', 'STU_010', 1790838000),
    ('EX_GT_01', 'STU_001', 1790838000);

INSERT INTO exam_result (exam_id, student_id, passed, score, recorded_at) VALUES
    ('EX_PIA_01', 'STU_001', TRUE, 85, 1792070100),
    ('EX_PIA_01', 'STU_002', TRUE, 72, 1792070100),
    ('EX_PIA_01', 'STU_003', FALSE, 48, 1792070100),
    ('EX_PIA_01', 'STU_004', FALSE, 0, 1792070100),
    ('EX_PIA_01', 'STU_005', TRUE, NULL, 1792070100),
    ('EX_VIO_01', 'STU_006', TRUE, 91, 1792156500),
    ('EX_VIO_01', 'STU_007', TRUE, 66, 1792156500),
    ('EX_VIO_01', 'STU_008', FALSE, 35, 1792156500),
    ('EX_VIO_01', 'STU_009', FALSE, NULL, 1792156500),
    ('EX_VIO_01', 'STU_010', TRUE, 78, 1792156500);

INSERT INTO charge (id, student_id, source_type, enrollment_id, activity_id, description, amount_cents, created_at) VALUES
    ('CHG_001', 'STU_001', 'PACKAGE', 'ENR_001', NULL, 'Starter 5 lessons', 18000, 1790838000),
    ('CHG_002', 'STU_002', 'PACKAGE', 'ENR_002', NULL, 'Standard 10 lessons', 34000, 1790838000),
    ('CHG_003', 'STU_003', 'PACKAGE', 'ENR_003', NULL, 'Progress 15 lessons', 48000, 1790838000),
    ('CHG_004', 'STU_004', 'PACKAGE', 'ENR_004', NULL, 'Advanced 20 lessons', 60000, 1790838000),
    ('CHG_005', 'STU_005', 'PACKAGE', 'ENR_005', NULL, 'Practice 8 lessons', 28000, 1790838000),
    ('CHG_006', 'STU_006', 'PACKAGE', 'ENR_006', NULL, 'Discovery 3 lessons', 12000, 1790838000),
    ('CHG_007', 'STU_007', 'PACKAGE', 'ENR_007', NULL, 'Intensive 12 lessons', 40000, 1790838000),
    ('CHG_008', 'STU_008', 'PACKAGE', 'ENR_008', NULL, 'Unlimited Basic', 60000, 1790838000),
    ('CHG_009', 'STU_009', 'PACKAGE', 'ENR_009', NULL, 'Unlimited Plus', 75000, 1790838000),
    ('CHG_010', 'STU_010', 'PACKAGE', 'ENR_010', NULL, 'Unlimited Premium', 90000, 1790838000),
    ('CHG_011', 'STU_010', 'ACTIVITY', NULL, 'LES_FL_01', 'Single paid flute lesson', 4000, 1790838000),
    ('CHG_012', 'STU_001', 'ACTIVITY', NULL, 'RENT_ROOM_01', 'Room rental 120 minutes', 4600, 1790838000),
    ('CHG_013', 'STU_002', 'ACTIVITY', NULL, 'RENT_DRM_01', 'Drums rental 90 minutes', 1650, 1790838000);

INSERT INTO payment (id, student_id, amount_cents, method, paid_at) VALUES
    ('PAY_001', 'STU_001', 9000, 'CARD', 1790924400),
    ('PAY_002', 'STU_002', 17000, 'TRANSFER', 1790924400),
    ('PAY_003', 'STU_003', 24000, 'CASH', 1790924400),
    ('PAY_004', 'STU_004', 30000, 'CARD', 1790924400),
    ('PAY_005', 'STU_005', 14000, 'TRANSFER', 1790924400),
    ('PAY_006', 'STU_006', 6000, 'CASH', 1790924400),
    ('PAY_007', 'STU_007', 20000, 'CARD', 1790924400),
    ('PAY_008', 'STU_008', 30000, 'TRANSFER', 1790924400),
    ('PAY_009', 'STU_009', 37500, 'CASH', 1790924400),
    ('PAY_010', 'STU_010', 45000, 'CARD', 1790924400);
