CREATE DATABASE conservatoire_oop CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs;

USE conservatoire_oop;

CREATE TABLE student(
 student_id VARCHAR(40) PRIMARY KEY,
 first_name VARCHAR(128) NOT NULL,
 last_name VARCHAR(128) NOT NULL,
 address VARCHAR(500) NOT NULL,
 birth_date DATE NOT NULL,
 phone VARCHAR(40) NOT NULL,
 email VARCHAR(255) NOT NULL,
 level VARCHAR(16) NOT NULL CHECK(level IN ('BEGINNER','INTERMEDIATE','ADVANCED'))
) ENGINE=InnoDB;

CREATE TABLE teacher(
    teacher_id VARCHAR(40) PRIMARY KEY,
    first_name VARCHAR(128) NOT NULL,
    last_name VARCHAR(128) NOT NULL,
    hourly_rate_cents BIGINT NOT NULL CHECK(hourly_rate_cents>=0),
    qualifications TEXT NOT NULL
) ENGINE=InnoDB;

CREATE TABLE student_preference(
    student_id VARCHAR(40) NOT NULL,
    instrument_type VARCHAR(16) NOT NULL,
    PRIMARY KEY(student_id,instrument_type),
    FOREIGN KEY(student_id) REFERENCES student(id)
) ENGINE=InnoDB;

CREATE TABLE teacher_specialization(
    teacher_id VARCHAR(40) NOT NULL,
    instrument_type VARCHAR(16) NOT NULL,
    PRIMARY KEY(teacher_id,instrument_type),
    FOREIGN KEY(teacher_id) REFERENCES teacher(id)
) ENGINE=InnoDB;

CREATE TABLE teacher_availability(
    teacher_id VARCHAR(40) NOT NULL,
    slot_number INT NOT NULL,
    starts_at BIGINT NOT NULL,
    ends_at BIGINT NOT NULL,
    PRIMARY KEY(teacher_id,slot_number),
    CHECK(ends_at>starts_at),
    FOREIGN KEY(teacher_id) REFERENCES teacher(id)
) ENGINE=InnoDB;

CREATE TABLE room(
    room_id VARCHAR(40) PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    capacity INT NOT NULL CHECK(capacity>0),
    hourly_price_cents BIGINT NOT NULL CHECK(hourly_price_cents>=0)
) ENGINE=InnoDB;

CREATE TABLE instrument(
    instrument_id VARCHAR(40) PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    instrument_type VARCHAR(16) NOT NULL,
    hourly_price_cents BIGINT NOT NULL CHECK(hourly_price_cents>=0)
) ENGINE=InnoDB;

CREATE TABLE package_offer(
    package_offer_id VARCHAR(40) PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    price_cents BIGINT NOT NULL CHECK(price_cents>=0),
    unlimited BOOLEAN NOT NULL,
    lesson_count INT,
    CHECK((unlimited=1 AND lesson_count IS NULL) OR (unlimited=0 AND lesson_count IS NOT NULL AND lesson_count>0))
    ) ENGINE=InnoDB;

CREATE TABLE enrollment(
 enrollment_id VARCHAR(40) PRIMARY KEY,
 student_id VARCHAR(40) NOT NULL,
 package_id VARCHAR(40) NOT NULL,
 valid_from DATE NOT NULL,
 valid_through DATE NOT NULL,
 purchased_at BIGINT NOT NULL,
 package_name VARCHAR(128) NOT NULL,
 price_cents BIGINT NOT NULL CHECK(price_cents>=0),
 unlimited BOOLEAN NOT NULL,
 lesson_count INT,
 UNIQUE(id,student_id),CHECK(valid_through>=valid_from),
 CHECK((unlimited=1 AND lesson_count IS NULL) OR (unlimited=0 AND lesson_count IS NOT NULL AND lesson_count>0)),
 FOREIGN KEY(student_id) REFERENCES student(id),
 FOREIGN KEY(package_id) REFERENCES package_offer(id)
) ENGINE=InnoDB;

CREATE TABLE activity(
 activity_id VARCHAR(40) PRIMARY KEY,
 kind VARCHAR(24) NOT NULL CHECK(kind IN ('LESSON','EXAM','ROOM_RENTAL','INSTRUMENT_RENTAL')),
 starts_at BIGINT NOT NULL,
 duration_minutes INT NOT NULL CHECK(duration_minutes>0),
 room_id VARCHAR(40),
 teacher_id VARCHAR(40),
 status VARCHAR(16) NOT NULL CHECK(status IN ('SCHEDULED','COMPLETED','CANCELLED')),
 changed_at BIGINT,
 instrument_type VARCHAR(16),
 group_lesson BOOLEAN,
 exam_name VARCHAR(128),
 exam_capacity INT,
 renter_id VARCHAR(40),
 rental_price_cents BIGINT,
 CHECK((status='SCHEDULED' AND changed_at IS NULL) OR (status<>'SCHEDULED' AND changed_at IS NOT NULL)),
 CHECK(kind<>'LESSON' OR (duration_minutes=60 AND room_id IS NOT NULL AND teacher_id IS NOT NULL AND instrument_type IS NOT NULL AND group_lesson IS NOT NULL)),
 CHECK(kind<>'EXAM' OR (exam_name IS NOT NULL AND instrument_type IS NOT NULL AND exam_capacity IS NOT NULL AND exam_capacity>0 AND room_id IS NOT NULL)),
 CHECK(kind NOT IN ('ROOM_RENTAL','INSTRUMENT_RENTAL') OR (renter_id IS NOT NULL AND rental_price_cents IS NOT NULL AND rental_price_cents>=0)),
 CHECK(kind<>'ROOM_RENTAL' OR room_id IS NOT NULL),
 CHECK(kind<>'INSTRUMENT_RENTAL' OR room_id IS NULL),
 FOREIGN KEY(room_id) REFERENCES room(id),
 FOREIGN KEY(teacher_id) REFERENCES teacher(id),FOREIGN KEY(renter_id) REFERENCES student(id)
) ENGINE=InnoDB;

CREATE TABLE activity_instrument(
    activity_id VARCHAR(40) NOT NULL,
    instrument_id VARCHAR(40) NOT NULL,
    PRIMARY KEY(activity_id,instrument_id),
    FOREIGN KEY(activity_id) REFERENCES activity(id),
    FOREIGN KEY(instrument_id) REFERENCES instrument(id)
) ENGINE=InnoDB;

CREATE TABLE lesson_participation(
 lesson_id VARCHAR(40) NOT NULL,
 student_id VARCHAR(40) NOT NULL,
 enrollment_id VARCHAR(40),
 minutes INT NOT NULL CHECK(minutes=60),
 state VARCHAR(16) NOT NULL CHECK(state IN ('RESERVED','CONSUMED','RELEASED')),
 reserved_at BIGINT NOT NULL,
 settled_at BIGINT,
 PRIMARY KEY(lesson_id,student_id),
 CHECK((state='RESERVED' AND settled_at IS NULL) OR (state<>'RESERVED' AND settled_at IS NOT NULL)),
 FOREIGN KEY(lesson_id) REFERENCES activity(id),
 FOREIGN KEY(student_id) REFERENCES student(id),
 FOREIGN KEY(enrollment_id,student_id) REFERENCES enrollment(id,student_id)
) ENGINE=InnoDB;

CREATE TABLE exam_registration(
    exam_id VARCHAR(40) NOT NULL,
    student_id VARCHAR(40) NOT NULL,
    registered_at BIGINT NOT NULL,
    PRIMARY KEY(exam_id,student_id),
    FOREIGN KEY(exam_id) REFERENCES activity(id),
    FOREIGN KEY(student_id) REFERENCES student(id)
) ENGINE=InnoDB;

CREATE TABLE exam_result(
    exam_id VARCHAR(40) NOT NULL,
    student_id VARCHAR(40) NOT NULL,
    passed BOOLEAN NOT NULL,
    score DECIMAL(10,4),
    recorded_at BIGINT NOT NULL,
    PRIMARY KEY(exam_id,student_id),
    CHECK(score IS NULL OR score>=0),
    FOREIGN KEY(exam_id,student_id) REFERENCES exam_registration(exam_id,student_id)
) ENGINE=InnoDB;

CREATE TABLE charge(
    charge_id VARCHAR(40) PRIMARY KEY,
    student_id VARCHAR(40) NOT NULL,
    source_type VARCHAR(16) NOT NULL,
    enrollment_id VARCHAR(40),
    activity_id VARCHAR(40),
    description TEXT NOT NULL,
    amount_cents BIGINT NOT NULL CHECK(amount_cents>=0),
    created_at BIGINT NOT NULL,
    UNIQUE(enrollment_id),
    UNIQUE(activity_id,student_id),
    CHECK((source_type='PACKAGE' AND enrollment_id IS NOT NULL AND activity_id IS NULL) OR (source_type='ACTIVITY' AND enrollment_id IS NULL AND activity_id IS NOT NULL)),
    FOREIGN KEY(student_id) REFERENCES student(id),FOREIGN KEY(enrollment_id,student_id) REFERENCES enrollment(id,student_id),FOREIGN KEY(activity_id) REFERENCES activity(id)
) ENGINE=InnoDB;

CREATE TABLE payment(
    payment_id VARCHAR(40) PRIMARY KEY,
    student_id VARCHAR(40) NOT NULL,
    amount_cents BIGINT NOT NULL CHECK(amount_cents>0),
    method VARCHAR(40) NOT NULL,
    paid_at BIGINT NOT NULL,
    FOREIGN KEY(student_id) REFERENCES student(id)
) ENGINE=InnoDB;

CREATE INDEX ix_activity_room_time ON activity(room_id,starts_at);
CREATE INDEX ix_activity_teacher_time ON activity(teacher_id,starts_at);
CREATE INDEX ix_usage_enrollment ON lesson_participation(enrollment_id,state);
CREATE INDEX ix_charge_student ON charge(student_id,created_at);
CREATE INDEX ix_payment_student ON payment(student_id,paid_at);
