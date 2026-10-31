package org.app.service;

import org.app.database.DbContext;
import org.app.database.DbLoader;
import org.app.model.*;
import org.app.repository.ExamRepository;
import org.app.repository.ResourceRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExamService {

    private final ExamRepository examRepository;
    private final ResourceRepository resourceRepository;

    public ExamService(ExamRepository examRepository, ResourceRepository resourceRepository) {
        this.examRepository = examRepository;
        this.resourceRepository = resourceRepository;
    }

//T07
    public List<Exam> getAllExams() throws SQLException {
        try (Connection connection = DbContext.open()) {
            return examRepository.findAllExams(connection);
        }
    }

    public Exam getExamById(String id) throws SQLException {
        try (Connection connection = DbContext.open()) {
            Exam exam = examRepository.findExamById(connection, id);
            if (exam == null) {
                throw new IllegalArgumentException("Exam not found: " + id);
            }
            return exam;
        }
    }

    public void createExam(Exam exam) throws SQLException {
        if (exam == null) {
            throw new IllegalArgumentException("Exam is required.");
        }
        try (Connection connection = DbContext.open()) {
            Room room = resourceRepository.findRoomById(connection, exam.getRoomId());
            if (room == null) {
                throw new IllegalArgumentException("Room not found: " + exam.getRoomId());
            }
            if (exam.getCapacity() > room.getCapacity()) {
                throw new IllegalArgumentException("Exam capacity " + exam.getCapacity()
                        + " exceeds room capacity " + room.getCapacity());
            }
            //T08
            List<String> conflicts = findConflicts(exam);
            if (!conflicts.isEmpty()) {
                throw new IllegalStateException("Schedule conflict: " + conflicts);
            }
            // Activity + instrument (N22)
            connection.setAutoCommit(false);
            try {
                examRepository.insertExam(connection, exam);
                for (String instrumentId : exam.getInstrumentIds()) {
                    examRepository.insertActivityInstrument(connection, exam.getId(), instrumentId);
                }
                connection.commit();
            } catch (SQLException | RuntimeException error) {
                connection.rollback();
                if (error instanceof SQLException sqlError && sqlError.getErrorCode() == 1062) {
                    throw new IllegalArgumentException("Exam ID already exists: " + exam.getId(), error);
                }
                throw error;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

//T08: Check duplicate
    public List<String> findConflicts(Exam exam) throws SQLException {
        List<String> conflicts = new ArrayList<>();
        String myRoom = exam.getRoomId();
        List<String> myInstruments = exam.getInstrumentIds();

        for (Activity other : DbLoader.loadActivities()) {
            if (other.getId().equals(exam.getId())) continue;
            if (other.getStatus() == Activity.Status.CANCELLED) continue;
            if (!other.overlaps(exam.getStart(), exam.getDurationMinutes())) continue;

            if (myRoom != null && myRoom.equals(roomOf(other))) {
                conflicts.add("Room " + myRoom + " busy: " + other);
            }
            List<String> otherInstruments = instrumentsOf(other);
            for (String instrumentId : myInstruments) {
                if (otherInstruments.contains(instrumentId)) {
                    conflicts.add("Instrument " + instrumentId + " busy: " + other);
                }
            }
        }
        return conflicts;
    }

//T09
    public void registerStudent(String examId, String studentId, LocalDateTime now) throws SQLException {
        try (Connection connection = DbContext.open()) {
            Exam exam = examRepository.findExamById(connection, examId);
            if (exam == null) {
                throw new IllegalArgumentException("Exam not found: " + examId);
            }
            if (!examRepository.studentExists(connection, studentId)) {
                throw new IllegalArgumentException("Student not found: " + studentId);
            }
            exam.register(studentId, now);
            // Check calendar
            for (Activity other : DbLoader.loadActivities()) {
                if (other.getId().equals(examId)) continue;
                if (other.getStatus() == Activity.Status.CANCELLED) continue;
                if (!other.overlaps(exam.getStart(), exam.getDurationMinutes())) continue;
                if (studentInvolved(other, studentId)) {
                    throw new IllegalStateException("Student has a conflicting activity: " + other);
                }
            }
            examRepository.insertRegistration(connection, new ExamRegistration(examId, studentId, now, null));
        }
    }

    public List<ExamRegistration> getRegistrations(String examId) throws SQLException {
        try (Connection connection = DbContext.open()) {
            if (examRepository.findExamById(connection, examId) == null) {
                throw new IllegalArgumentException("Exam not found: " + examId);
            }
            return examRepository.findRegistrations(connection, examId);
        }
    }

//T10
    public void completeExam(String examId, LocalDateTime now) throws SQLException {
        try (Connection connection = DbContext.open()) {
            Exam exam = examRepository.findExamById(connection, examId);
            if (exam == null) {
                throw new IllegalArgumentException("Exam not found: " + examId);
            }
            exam.complete(now);
            int rows = examRepository.updateStatus(connection, examId, "COMPLETED", now);
            if (rows == 0) {
                throw new IllegalStateException("Exam could not be completed (not scheduled anymore).");
            }
        }
    }

    public void recordResult(String examId, String studentId, boolean passed, BigDecimal score, LocalDateTime now) throws SQLException {
        try (Connection connection = DbContext.open()) {
            Exam exam = examRepository.findExamById(connection, examId);
            if (exam == null) {
                throw new IllegalArgumentException("Exam not found: " + examId);
            }
            exam.recordResult(studentId, passed, score, now);
            try {
                examRepository.insertResult(connection, examId, studentId, new ExamResult(passed, score, now));
            } catch (SQLException error) {
                if (error.getErrorCode() == 1062) {
                    throw new IllegalArgumentException("Result already recorded for student: " + studentId, error);
                }
                throw error;
            }
        }
    }

    // ---- helper (polymorphism) ----
    private String roomOf(Activity activity) {
        if (activity instanceof Lesson lesson) return lesson.getRoomId();
        if (activity instanceof Exam exam) return exam.getRoomId();
        if (activity instanceof Rental rental && rental.getResourceType() == Rental.ResourceType.ROOM) {
            return rental.getResourceId();
        }
        return null;
    }

    private List<String> instrumentsOf(Activity activity) {
        if (activity instanceof Lesson lesson) return lesson.getInstrumentIds();
        if (activity instanceof Exam exam) return exam.getInstrumentIds();
        List<String> result = new ArrayList<>();
        if (activity instanceof Rental rental && rental.getResourceType() == Rental.ResourceType.INSTRUMENT) {
            result.add(rental.getResourceId());
        }
        return result;
    }

    private boolean studentInvolved(Activity activity, String studentId) {
        if (activity instanceof Lesson lesson) {
            for (LessonParticipation p : lesson.getParticipants()) {
                if (p.getStudentId().equals(studentId)) return true;
            }
            return false;
        }
        if (activity instanceof Exam exam) {
            for (ExamRegistration r : exam.getRegistrations()) {
                if (r.getStudentId().equals(studentId)) return true;
            }
            return false;
        }
        if (activity instanceof Rental rental) {
            return rental.getStudentId().equals(studentId);
        }
        return false;
    }
}
