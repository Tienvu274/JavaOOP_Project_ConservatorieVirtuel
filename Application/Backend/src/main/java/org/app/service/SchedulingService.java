package org.app.service;

import org.app.database.DbContext;
import org.app.database.DbTime;
import org.app.model.*;
import org.app.repository.ChargeRepository;
import org.app.repository.LessonRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SchedulingService {

    public static final long SINGLE_LESSON_PRICE_CENTS = 4000;

    private final LessonRepository lessonRepository;
    private final ChargeRepository chargeRepository;

    public SchedulingService(LessonRepository lessonRepository, ChargeRepository chargeRepository) {
        this.lessonRepository = lessonRepository;
        this.chargeRepository = chargeRepository;
    }

    public Lesson scheduleLesson(Lesson draft, LocalDateTime now) throws SQLException {
        require(draft != null && now != null, "Lesson and current time are required.");
        require(draft.getStatus() == Activity.Status.SCHEDULED, "A new lesson must be scheduled.");
        Lesson lesson = new Lesson(
                draft.getId(),
                draft.getStart(),
                draft.getTeacherId(),
                draft.getRoomId(),
                draft.getInstrumentType(),
                draft.isGroupLesson(),
                draft.getInstrumentIds(),
                Activity.Status.SCHEDULED,
                null,
                new ArrayList<>()
        );

        for (LessonParticipation participant : draft.getParticipants()) {
            lesson.addParticipant(new LessonParticipation(
                            lesson.getId(),
                            participant.getStudentId(),
                            participant.getEnrollmentId(),
                            60,
                            LessonParticipation.State.RESERVED,
                            now,
                            null
                    )
            );
        }

        validateLesson(lesson, now);

        try (Connection connection = DbContext.open()) {
            connection.setAutoCommit(false);
            try {
                lessonRepository.insert(connection, lesson);
                for (LessonParticipation participant : lesson.getParticipants()) {
                    if (participant.getEnrollmentId() == null) {
                        Charge charge = new Charge(
                                chargeRepository.nextId(connection),
                                participant.getStudentId(),
                                "ACTIVITY",
                                null,
                                lesson.getId(),
                                "Single paid lesson: "
                                        + lesson.getInstrumentType(),
                                SINGLE_LESSON_PRICE_CENTS,
                                now
                        );
                        chargeRepository.insert(connection, charge);
                    }
                }
                connection.commit();
                return lesson;
            } catch (SQLException | RuntimeException error) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackError) {
                    error.addSuppressed(rollbackError);
                }
                throw error;
            }
        }
    }

    public void validateLesson(Lesson lesson, LocalDateTime now) throws SQLException {

        require(lesson.getStart().isAfter(now), "Lesson must start in the future.");
        DbTime.toEpochSeconds(lesson.getStart());
        DbTime.toEpochSeconds(lesson.getEnd());
        int count = lesson.getParticipants().size();
        require(lesson.isGroupLesson() ? count >= 2 : count == 1, "Individual lesson needs 1 student group lesson needs at least 2.");
        Teacher teacher = null;
        for (Teacher value : lessonRepository.loadTeachers()) {
            if (value.getId().equals(lesson.getTeacherId())) {
                teacher = value;
                break;
            }
        }

        require(teacher != null, "Teacher not found.");
        require(teacher.canTeach(lesson.getInstrumentType()), "Teacher has the wrong specialization.");
        require(teacher.canTeach(lesson.getInstrumentType(),lesson.getStart(), lesson.getEnd()), "Teacher is not available for the whole lesson.");
        Room room = null;
        for (Room value : lessonRepository.loadRooms()) {
            if (value.getId().equals(lesson.getRoomId())) {
                room = value;
                break;
            }
        }

        require(room != null, "Room not found.");
        require(count <= room.getCapacity(), "Room capacity exceeded.");

        List<Instrument> instruments = lessonRepository.loadInstruments();

        for (String id : lesson.getInstrumentIds()) {
            Instrument instrument = null;
            for (Instrument value : instruments) {
                if (value.getId().equals(id)) {
                    instrument = value;
                    break;
                }
            }
            require(instrument != null, "Instrument not found: " + id);
            require(instrument.getType().equals(lesson.getInstrumentType()), "Wrong instrument type: " + id);
        }

        List<Student> students = lessonRepository.loadStudents();
        List<Enrollment> enrollments = lessonRepository.loadEnrollments();
        List<LessonParticipation> history = lessonRepository.loadParticipations();

        for (LessonParticipation participant : lesson.getParticipants()) {
            boolean exists = false;
            for (Student student : students) {
                if (student.getId().equals(participant.getStudentId())) {
                    exists = true;
                    break;
                }
            }
            require(exists, "Student not found: " + participant.getStudentId());
            if (participant.getEnrollmentId() == null) {
                continue;
            }
            Enrollment enrollment = null;
            for (Enrollment value : enrollments) {
                if (value.getId().equals(participant.getEnrollmentId())) {
                    enrollment = value;
                    break;
                }
            }

            require(enrollment != null, "Enrollment not found: " + participant.getEnrollmentId());
            require(enrollment.getStudentId().equals(participant.getStudentId()), "Enrollment belongs to another student.");
            require(enrollment.isValidOn(lesson.getStart().toLocalDate())
                            && enrollment.isValidOn(lesson.getEnd().minusNanos(1).toLocalDate()), "The whole lesson must be within the package validity dates.");
            Integer available = enrollment.availableMinutes(lesson.getStart().toLocalDate(), history);
            require(available == null || available >= 60, "Not enough package minutes: " + participant.getStudentId());
        }
        checkConflicts(lesson, lessonRepository.loadActivities());
    }

    public void checkConflicts(Lesson lesson, List<Activity> activities) {
        List<String> studentIds = studentsOf(lesson);
        for (Activity other : activities) {
            if (lesson.getId().equals(other.getId())) {
                throw new IllegalArgumentException("Activity ID already exists: " + lesson.getId());
            }
            if (other.getStatus() == Activity.Status.CANCELLED) continue;
            if (!other.overlaps(lesson.getStart(), lesson.getDurationMinutes())) continue;
            if (lesson.getRoomId().equals(roomOf(other))) {
                throw new IllegalArgumentException("Room conflict with " + other.getId());
            }
            if (other instanceof Lesson) {
                Lesson otherLesson = (Lesson) other;
                if (lesson.getTeacherId().equals(otherLesson.getTeacherId())) {
                    throw new IllegalArgumentException("Teacher conflict with " + other.getId());
                }
            }
            List<String> instrumentIds = instrumentsOf(other);
            for (String id : lesson.getInstrumentIds()) {
                if (instrumentIds.contains(id)) {
                    throw new IllegalArgumentException("Instrument conflict with " + other.getId());
                }
            }
            List<String> otherStudentIds = studentsOf(other);
            for (String id : studentIds) {
                if (otherStudentIds.contains(id)) {
                    throw new IllegalArgumentException("Student conflict with " + other.getId());
                }
            }
        }
    }

    private String roomOf(Activity activity) {
        if (activity instanceof Lesson) {
            return ((Lesson) activity).getRoomId();
        }

        if (activity instanceof Exam) {
            return ((Exam) activity).getRoomId();
        }

        if (activity instanceof Rental) {
            Rental rental = (Rental) activity;

            if (rental.getResourceType() == Rental.ResourceType.ROOM) {
                return rental.getResourceId();
            }
        }
        return null;
    }

    private List<String> instrumentsOf(Activity activity) {
        if (activity instanceof Lesson) {
            return ((Lesson) activity).getInstrumentIds();
        }

        if (activity instanceof Exam) {
            return ((Exam) activity).getInstrumentIds();
        }

        List<String> ids = new ArrayList<>();

        if (activity instanceof Rental) {
            Rental rental = (Rental) activity;

            if (rental.getResourceType() == Rental.ResourceType.INSTRUMENT) {
                ids.add(rental.getResourceId());
            }
        }

        return ids;
    }

    private List<String> studentsOf(Activity activity) {
        List<String> ids = new ArrayList<>();
        if (activity instanceof Lesson) {
            Lesson lesson = (Lesson) activity;
            for (LessonParticipation participant : lesson.getParticipants()) {
                if (participant.getState() != LessonParticipation.State.RELEASED) {
                    ids.add(participant.getStudentId());
                }
            }
        } else if (activity instanceof Exam) {
            for (ExamRegistration registration : ((Exam) activity).getRegistrations()) {
                ids.add(registration.getStudentId());
            }
        } else if (activity instanceof Rental) {
            ids.add(((Rental) activity).getStudentId());
        }
        return ids;
    }

    private void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    public Lesson cancelLesson(String lessonId, LocalDateTime now) throws SQLException {
        if (lessonId == null || !lessonId.matches("[A-Za-z0-9_-]{1,40}")) {
            throw new IllegalArgumentException("Invalid lesson ID.");
        }
        if (now == null) throw new IllegalArgumentException("Current time is required.");
        Lesson lesson = null;
        for (Activity activity : lessonRepository.loadActivities()) {
            if (activity.getId().equals(lessonId) && activity instanceof Lesson) {
                lesson = (Lesson) activity;
                break;
            }
        }
        if (lesson == null) throw new IllegalArgumentException("Lesson not found: " + lessonId);
        if (lesson.getStatus() != Activity.Status.SCHEDULED) {
            throw new IllegalStateException("Only scheduled lessons can be cancelled.");
        }
        if (!now.isBefore(lesson.getStart())) {
            throw new IllegalStateException("Cannot cancel a lesson at or after its start.");
        }
        lesson.cancel(now);
        long secondsLeft = DbTime.toEpochSeconds(lesson.getStart()) - DbTime.toEpochSeconds(now);
        LessonParticipation.State state = LessonParticipation.State.RELEASED;
        if (secondsLeft < 24 * 60 * 60) state = LessonParticipation.State.CONSUMED;

        List<LessonParticipation> participants = new ArrayList<>();
        for (LessonParticipation p : lesson.getParticipants()) {
            participants.add(p.settle(state, now));
        }

        Lesson cancelled = new Lesson(lesson.getId(), lesson.getStart(), lesson.getTeacherId(), lesson.getRoomId(), lesson.getInstrumentType(), lesson.isGroupLesson(), lesson.getInstrumentIds(), lesson.getStatus(), lesson.getChangedAt(), participants);

        try (Connection connection = DbContext.open()) {
            connection.setAutoCommit(false);
            try {
                lessonRepository.cancel(connection, cancelled, state);
                connection.commit();
                return cancelled;
            } catch (SQLException | RuntimeException error) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackError) {
                    error.addSuppressed(rollbackError);
                }
                throw error;
            }
        }
    }

    public Lesson completeLesson(String lessonId, LocalDateTime now) throws SQLException {
        if (lessonId == null || !lessonId.matches("[A-Za-z0-9_-]{1,40}")) {
            throw new IllegalArgumentException("Invalid lesson ID.");
        }
        if (now == null) throw new IllegalArgumentException("Current time is required.");
        Lesson lesson = null;
        for (Activity activity : lessonRepository.loadActivities()) {
            if (activity.getId().equals(lessonId) && activity instanceof Lesson) {
                lesson = (Lesson) activity;
                break;
            }
        }

        if (lesson == null) throw new IllegalArgumentException("Lesson not found: " + lessonId);
        if (lesson.getStatus() != Activity.Status.SCHEDULED) {
            throw new IllegalStateException("Only scheduled lessons can be completed.");
        }
        if (now.isBefore(lesson.getEnd())) {
            throw new IllegalStateException("Lesson has not ended yet.");
        }
        lesson.complete(now);

        List<LessonParticipation> participants = new ArrayList<>();
        for (LessonParticipation p : lesson.getParticipants()) {
            participants.add(p.settle(LessonParticipation.State.CONSUMED, now));
        }

        Lesson completed = new Lesson(lesson.getId(), lesson.getStart(), lesson.getTeacherId(), lesson.getRoomId(), lesson.getInstrumentType(), lesson.isGroupLesson(), lesson.getInstrumentIds(), lesson.getStatus(), lesson.getChangedAt(), participants);

        try (Connection connection = DbContext.open()) {
            connection.setAutoCommit(false);
            try {
                lessonRepository.complete(connection, completed);
                connection.commit();
                return completed;
            } catch (SQLException | RuntimeException error) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackError) {
                    error.addSuppressed(rollbackError);
                }
                throw error;
            }
        }
    }

    public List<Activity> getTeacherActivities(String teacherId) throws SQLException {
        require(teacherId != null && teacherId.matches("[A-Za-z0-9_-]{1,40}"), "Invalid teacher ID.");

        boolean exists = false;
        for (Teacher teacher : lessonRepository.loadTeachers()) {
            if (teacher.getId().equals(teacherId)) {
                exists = true;
                break;
            }
        }
        require(exists, "Teacher not found: " + teacherId);

        List<Activity> result = new ArrayList<>();

        for (Activity activity : lessonRepository.loadActivities()) {
            if (activity instanceof Lesson) {
                Lesson lesson = (Lesson) activity;
                if (lesson.getTeacherId().equals(teacherId)) result.add(lesson);
            }
        }

        return result;
    }

    public List<Activity> getStudentActivities(String studentId) throws SQLException {
        require(studentId != null && studentId.matches("[A-Za-z0-9_-]{1,40}"), "Invalid student ID.");

        boolean exists = false;
        for (Student student : lessonRepository.loadStudents()) {
            if (student.getId().equals(studentId)) {
                exists = true;
                break;
            }
        }
        require(exists, "Student not found: " + studentId);

        List<Activity> result = new ArrayList<>();

        for (Activity activity : lessonRepository.loadActivities()) {
            boolean belongsToStudent = false;

            if (activity instanceof Lesson) {
                for (LessonParticipation p : ((Lesson) activity).getParticipants()) {
                    if (p.getStudentId().equals(studentId)) {
                        belongsToStudent = true;
                        break;
                    }
                }
            } else if (activity instanceof Exam) {
                for (ExamRegistration r : ((Exam) activity).getRegistrations()) {
                    if (r.getStudentId().equals(studentId)) {
                        belongsToStudent = true;
                        break;
                    }
                }
            } else if (activity instanceof Rental) {
                belongsToStudent = ((Rental) activity).getStudentId().equals(studentId);
            }

            if (belongsToStudent) {
                result.add(activity);
            }
        }

        return result;
    }
}