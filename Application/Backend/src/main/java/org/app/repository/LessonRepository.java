package org.app.repository;

import org.app.database.DbLoader;
import org.app.database.DbTime;
import org.app.model.*;

import java.sql.*;
import java.util.List;

public class LessonRepository {

    public List<Teacher> loadTeachers() throws SQLException {
        return DbLoader.loadTeachers();
    }

    public List<Room> loadRooms() throws SQLException {
        return DbLoader.loadRooms();
    }

    public List<Instrument> loadInstruments() throws SQLException {
        return DbLoader.loadInstruments();
    }

    public List<Student> loadStudents() throws SQLException {
        return DbLoader.loadStudents();
    }

    public List<Enrollment> loadEnrollments() throws SQLException {
        return DbLoader.loadEnrollments();
    }

    public List<LessonParticipation> loadParticipations() throws SQLException {
        return DbLoader.loadParticipations();
    }

    public List<Activity> loadActivities() throws SQLException {
        return DbLoader.loadActivities();
    }

    public void insert(Connection connection, Lesson lesson) throws SQLException {
        String activitySql = "INSERT INTO activity "
                + "(id, kind, starts_at, duration_minutes, "
                + "room_id, teacher_id, status, changed_at, "
                + "instrument_type, group_lesson) "
                + "VALUES (?, 'LESSON', ?, 60, ?, ?, "
                + "'SCHEDULED', NULL, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(activitySql)) {
            statement.setString(1, lesson.getId());
            statement.setLong(2, DbTime.toEpochSeconds(lesson.getStart()));
            statement.setString(3, lesson.getRoomId());
            statement.setString(4, lesson.getTeacherId());
            statement.setString(5, lesson.getInstrumentType());
            statement.setBoolean(6, lesson.isGroupLesson());
            statement.executeUpdate();
        }

        String instrumentSql = "INSERT INTO activity_instrument "
                        + "(activity_id, instrument_id) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(instrumentSql)) {
            for (String instrumentId : lesson.getInstrumentIds()) {
                statement.setString(1, lesson.getId());
                statement.setString(2, instrumentId);
                statement.executeUpdate();
            }
        }

        String participantSql = "INSERT INTO lesson_participation "
                        + "(lesson_id, student_id, enrollment_id, "
                        + "minutes, state, reserved_at, settled_at) "
                        + "VALUES (?, ?, ?, 60, 'RESERVED', ?, NULL)";

        try (PreparedStatement statement = connection.prepareStatement(participantSql)) {
            for (LessonParticipation participant : lesson.getParticipants()) {
                statement.setString(1, lesson.getId());
                statement.setString(2, participant.getStudentId());
                statement.setString(3, participant.getEnrollmentId());
                statement.setLong(4, DbTime.toEpochSeconds(participant.getReservedAt()));
                statement.executeUpdate();
            }
        }
    }
}