-- Fictional fixed-date data for checking DB/entity mapping.
-- Currency: EUR cents.
-- Duration: minutes.
-- Timestamps: UTC epoch seconds.

INSERT INTO student VALUES
                        ('S1','An','Nguyen','Paris','2008-04-12',
                         '0600000001','an@example.com','BEGINNER'),

                        ('S2','Binh','Tran','Chelles','2007-11-20',
                         '0600000002','binh@example.com','INTERMEDIATE');

INSERT INTO teacher VALUES
                        ('T1','Minh','Le',3000,'Piano diploma'),
                        ('T2','Emma','Martin',3500,'Violin diploma');

INSERT INTO room VALUES
                     ('R1','Piano Room',5,1500),
                     ('R2','Rehearsal Room',12,2200);

INSERT INTO instrument VALUES
                           ('I1','Grand Piano','PIANO',1200),
                           ('I2','Violin','VIOLIN',900);

INSERT INTO package_offer VALUES
                              ('P5','5 lessons',18000,FALSE,5),
                              ('PU','Unlimited',60000,TRUE,NULL);

INSERT INTO student_preference VALUES
                                   ('S1','PIANO'),
                                   ('S2','VIOLIN');

INSERT INTO teacher_specialization VALUES
                                       ('T1','PIANO'),
                                       ('T2','VIOLIN');

INSERT INTO teacher_availability VALUES
                                     ('T1',0,1790812800,1830297540),
                                     ('T2',0,1790812800,1830297540);

INSERT INTO enrollment VALUES
                           (
                               'EN1','S1','P5',
                               '2026-10-01','2027-01-31',
                               1790841600,
                               '5 lessons',18000,FALSE,5
                           ),
                           (
                               'EN2','S2','PU',
                               '2026-10-01','2027-01-31',
                               1790841600,
                               'Unlimited',60000,TRUE,NULL
                           );

INSERT INTO activity VALUES
                         (
                             'L1','LESSON',
                             1793696400,60,
                             'R1','T1',
                             'SCHEDULED',NULL,
                             'PIANO',TRUE,
                             NULL,NULL,NULL,NULL
                         ),
                         (
                             'RENT1','ROOM_RENTAL',
                             1793782800,120,
                             'R1',NULL,
                             'SCHEDULED',NULL,
                             NULL,NULL,
                             NULL,NULL,
                             'S1',3000
                         ),
                         (
                             'L2','LESSON',
                             1793869200,60,
                             'R2','T2',
                             'SCHEDULED',NULL,
                             'VIOLIN',FALSE,
                             NULL,NULL,NULL,NULL
                         ),
                         (
                             'EX1','EXAM',
                             1790928000,60,
                             'R2',NULL,
                             'COMPLETED',1790931600,
                             'VIOLIN',NULL,
                             'Previous violin exam',2,
                             NULL,NULL
                         );

INSERT INTO activity_instrument VALUES
                                    ('L1','I1'),
                                    ('L2','I2'),
                                    ('EX1','I2');

INSERT INTO lesson_participation VALUES
                                     (
                                         'L1','S1','EN1',
                                         60,'RESERVED',
                                         1790841600,NULL
                                     ),
                                     (
                                         'L1','S2','EN2',
                                         60,'RESERVED',
                                         1790841600,NULL
                                     ),
                                     (
                                         'L2','S2',NULL,
                                         60,'RESERVED',
                                         1790841600,NULL
                                     );

INSERT INTO exam_registration VALUES
    ('EX1','S1',1790841600);

INSERT INTO exam_result VALUES
    ('EX1','S1',TRUE,85.0000,1790931600);

INSERT INTO charge VALUES
                       (
                           'CH1','S1','PACKAGE',
                           'EN1',NULL,
                           '5 lesson package',
                           18000,1790841600
                       ),
                       (
                           'CH2','S2','PACKAGE',
                           'EN2',NULL,
                           'Unlimited package',
                           60000,1790841600
                       ),
                       (
                           'CH3','S1','ACTIVITY',
                           NULL,'RENT1',
                           'Room rental',
                           3000,1790841600
                       ),
                       (
                           'CH4','S2','ACTIVITY',
                           NULL,'L2',
                           'Single paid lesson',
                           4000,1790841600
                       );

INSERT INTO payment VALUES
                        ('PAY1','S1',4000,'CARD',1790841600),
                        ('PAY2','S2',5000,'TRANSFER',1790841600);