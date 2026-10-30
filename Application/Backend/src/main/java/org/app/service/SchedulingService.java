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
            require(!lesson.getId().equals(other.getId()), "Activity ID already exists.");
            if (other.getStatus() == Activity.Status.CANCELLED || !other.overlaps(lesson.getStart(), 60)) {
                continue;
            }
            require(!lesson.getRoomId().equals(roomOf(other)), "Room conflict with " + other.getId());
            if (other instanceof Lesson) {
                Lesson otherLesson = (Lesson) other;
                require(!lesson.getTeacherId().equals(otherLesson.getTeacherId()), "Teacher conflict with " + other.getId());
            }

            for (String id : lesson.getInstrumentIds()) {
                require(!instrumentsOf(other).contains(id), "Instrument conflict with " + other.getId());
            }

            for (String id : studentIds) {
                require(!studentsOf(other).contains(id), "Student conflict with " + other.getId()
                );
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
}