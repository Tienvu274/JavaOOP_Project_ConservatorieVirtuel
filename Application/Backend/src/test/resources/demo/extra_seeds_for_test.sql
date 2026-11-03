use conservatoire_oop;
-- Existing project database and original seed must already be loaded.
-- Select that database in Workbench before running. No original row is changed.
-- One use per rehearsal. Keep new IDs unused. Do not DROP or reseed.
-- Dates printed here use Paris winter time (November 2026, UTC+01:00).
SET time_zone = '+01:00';
SELECT DATABASE() AS selected_database;
SET @live_now = FLOOR(UNIX_TIMESTAMP()/60)*60;
SET @lesson_offset_hours = 4; -- If conflict preflight fails, ROLLBACK and try 6 or 8.
SET @live_start = @live_now + @lesson_offset_hours*3600;
SET @availability_slot = (SELECT COALESCE(MAX(slot_number),0)+1 FROM teacher_availability WHERE teacher_id='TCH_002');

START TRANSACTION;
use conservatoire_oop;
-- Minimal preparation: original teacher can teach the live lesson today.
INSERT INTO teacher_availability(teacher_id,slot_number,starts_at,ends_at)
VALUES('TCH_002',@availability_slot,@live_now-3600,@live_now+86400);

-- A separate historical session is needed to RECORD a new result immediately.
-- Seed completed exams already have results for their existing candidates.
INSERT INTO activity(id,kind,starts_at,duration_minutes,room_id,status,changed_at,instrument_type,exam_name,exam_capacity)
VALUES('EX_LIVE_DONE','EXAM',@live_now-10800,30,'RM_GT_02','SCHEDULED',NULL,'GUITAR','Guitar progress assessment',2);
INSERT INTO activity_instrument(activity_id,instrument_id) VALUES('EX_LIVE_DONE','GT_02');
INSERT INTO exam_registration(exam_id,student_id,registered_at)
VALUES('EX_LIVE_DONE','STU_001',@live_now-86400);

-- Expected package: 12000 cents, finite 3 lessons.
SELECT id,name,price_cents,unlimited,lesson_count FROM package_offer WHERE id='PKG_INTRO_03';
-- Partner's available minutes are 540 in untouched seed (600 purchased - 60 consumed).
-- On a used database, this displayed number must still be >=60.
SELECT e.id,e.student_id,e.lesson_count*60 - COALESCE(SUM(CASE WHEN p.state<>'RELEASED' THEN p.minutes ELSE 0 END),0) AS available_before_demo,
       e.valid_from,e.valid_through
FROM enrollment e LEFT JOIN lesson_participation p ON p.enrollment_id=e.id
WHERE e.id='ENR_002'
GROUP BY e.id,e.student_id,e.lesson_count,e.valid_from,e.valid_through;

-- Seed future exam must remain SCHEDULED, future, and not full.
SELECT a.id,a.status,FROM_UNIXTIME(a.starts_at) AS paris_start,a.exam_capacity,
       COUNT(r.student_id) AS registered,
       (a.status='SCHEDULED' AND a.starts_at>@live_now AND COUNT(r.student_id)<a.exam_capacity) AS ready_for_registration
FROM activity a LEFT JOIN exam_registration r ON r.exam_id=a.id
WHERE a.id='EX_FL_01'
GROUP BY a.id,a.status,a.starts_at,a.exam_capacity;

-- Must be ZERO: prevent accidental reuse of IDs from a prior rehearsal.
SELECT (SELECT COUNT(*) FROM student WHERE id='STU_LIVE_01')+
       (SELECT COUNT(*) FROM enrollment WHERE id='ENR_LIVE_01')+
       (SELECT COUNT(*) FROM charge WHERE id='CHG_LIVE_01')+
       (SELECT COUNT(*) FROM activity WHERE id IN ('LES_LIVE_01','LES_LIVE_CLASH')) AS reused_live_ids;

-- Must be ZERO: existing activities blocking the planned group lesson.
SELECT COUNT(*) AS blocking_activities
FROM activity a
WHERE a.status<>'CANCELLED' AND a.starts_at<@live_start+3600 AND @live_start<a.starts_at+a.duration_minutes*60
AND (a.teacher_id='TCH_002' OR a.room_id='RM_GT_02' OR a.renter_id='STU_002'
 OR EXISTS(SELECT 1 FROM activity_instrument i WHERE i.activity_id=a.id AND i.instrument_id='GT_02')
 OR EXISTS(SELECT 1 FROM lesson_participation p WHERE p.lesson_id=a.id AND p.student_id='STU_002')
 OR EXISTS(SELECT 1 FROM exam_registration r WHERE r.exam_id=a.id AND r.student_id='STU_002'));

SELECT DATE_FORMAT(FROM_UNIXTIME(@live_start),'%Y-%m-%d %H:%i') AS lesson_start,
       DATE_FORMAT(FROM_UNIXTIME(@live_start+1800),'%Y-%m-%d %H:%i') AS conflict_start,
       CURRENT_DATE() AS valid_from,DATE_ADD(CURRENT_DATE(),INTERVAL 30 DAY) AS valid_through,
       DATE_FORMAT(DATE_ADD(FROM_UNIXTIME(@live_now),INTERVAL 7 DAY),'%Y-%m-%d 08:00') AS extra_availability_start,
       DATE_FORMAT(DATE_ADD(FROM_UNIXTIME(@live_now),INTERVAL 7 DAY),'%Y-%m-%d 10:00') AS extra_availability_end;

-- No COMMIT here. Only if ALL statements succeed and preflights are valid,
-- execute COMMIT separately in this SAME tab. On any failure: ROLLBACK.
-- New candidate, enrollment, payment and lesson must be created LIVE in the app.
commit;