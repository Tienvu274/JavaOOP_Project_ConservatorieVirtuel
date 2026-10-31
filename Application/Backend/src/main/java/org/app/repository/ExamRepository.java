package org.app.repository;

import org.app.database.DbTime;
import org.app.model.Exam;
import org.app.model.ExamRegistration;
import org.app.model.ExamResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExamRepository {

//T07
    public List<Exam> findAllExams(Connection connection) throws SQLException {
        List<Exam> exams = new ArrayList<>();
        String sql = "SELECT * FROM activity WHERE kind = 'EXAM' ORDER BY starts_at, id";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet row = statement.executeQuery()) {
            while (row.next()) {
                exams.add(readExam(connection, row));
            }
        }
        return exams;
    }

    public Exam findExamById(Connection connection, String id) throws SQLException {
        String sql = "SELECT * FROM activity WHERE id = ? AND kind = 'EXAM'";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            try (ResultSet row = statement.executeQuery()) {
                if (row.next()) {
                    return readExam(connection, row);
                }
                return null;
            }
        }
    }

    private Exam readExam(Connection connection, ResultSet row) throws SQLException {
        String id = row.getString("id");
        return new Exam(
                id,
                row.getString("exam_name"),
                row.getString("instrument_type"),
                row.getString("room_id"),
                DbTime.read(row, "starts_at"),
                row.getInt("duration_minutes"),
                row.getInt("exam_capacity"),
                findInstrumentIds(connection, id),
                Exam.Status.valueOf(row.getString("status")),
                DbTime.read(row, "changed_at"),
                findRegistrations(connection, id)
        );
    }

    private List<String> findInstrumentIds(Connection connection, String activityId) throws SQLException {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT instrument_id FROM activity_instrument WHERE activity_id = ? ORDER BY instrument_id";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, activityId);
            try (ResultSet row = statement.executeQuery()) {
                while (row.next()) {
                    ids.add(row.getString("instrument_id"));
                }
            }
        }
        return ids;
    }

    public List<ExamRegistration> findRegistrations(Connection connection, String examId) throws SQLException {
        List<ExamRegistration> list = new ArrayList<>();
        String sql = "SELECT * FROM exam_registration WHERE exam_id = ? ORDER BY student_id";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, examId);
            try (ResultSet row = statement.executeQuery()) {
                while (row.next()) {
                    String studentId = row.getString("student_id");
                    list.add(new ExamRegistration(
                            examId,
                            studentId,
                            DbTime.read(row, "registered_at"),
                            findResult(connection, examId, studentId)
                    ));
                }
            }
        }
        return list;
    }

    private ExamResult findResult(Connection connection, String examId, String studentId) throws SQLException {
        String sql = "SELECT * FROM exam_result WHERE exam_id = ? AND student_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, examId);
            statement.setString(2, studentId);
            try (ResultSet row = statement.executeQuery()) {
                if (row.next()) {
                    return new ExamResult(
                            row.getBoolean("passed"),
                            row.getBigDecimal("score"),      // trả null nếu cột NULL
                            DbTime.read(row, "recorded_at")
                    );
                }
                return null;
            }
        }
    }

    public boolean studentExists(Connection connection, String studentId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT id FROM student WHERE id = ?")) {
            statement.setString(1, studentId);
            try (ResultSet row = statement.executeQuery()) {
                return row.next();
            }
        }
    }
    public void insertExam(Connection connection, Exam exam) throws SQLException {
        String sql = "INSERT INTO activity "
                + "(id, kind, starts_at, duration_minutes, room_id, status, changed_at, exam_name, exam_capacity, instrument_type) "
                + "VALUES (?, 'EXAM', ?, ?, ?, 'SCHEDULED', NULL, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, exam.getId());
            statement.setLong(2, DbTime.toEpochSeconds(exam.getStart()));
            statement.setInt(3, exam.getDurationMinutes());
            statement.setString(4, exam.getRoomId());
            statement.setString(5, exam.getName());
            statement.setInt(6, exam.getCapacity());
            statement.setString(7, exam.getInstrumentType());
            if (statement.executeUpdate() != 1) {
                throw new SQLException("Exam was not saved.");
            }
        }
    }

    public void insertActivityInstrument(Connection connection, String activityId, String instrumentId) throws SQLException {
        String sql = "INSERT INTO activity_instrument (activity_id, instrument_id) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, activityId);
            statement.setString(2, instrumentId);
            statement.executeUpdate();
        }
    }

//T09
    public void insertRegistration(Connection connection, ExamRegistration registration) throws SQLException {
        String sql = "INSERT INTO exam_registration (exam_id, student_id, registered_at) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, registration.getExamId());
            statement.setString(2, registration.getStudentId());
            statement.setLong(3, DbTime.toEpochSeconds(registration.getRegisteredAt()));
            statement.executeUpdate();
        }
    }

//T10
    public int updateStatus(Connection connection, String examId, String status, java.time.LocalDateTime changedAt) throws SQLException {
        String sql = "UPDATE activity SET status = ?, changed_at = ? WHERE id = ? AND kind = 'EXAM' AND status = 'SCHEDULED'";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setLong(2, DbTime.toEpochSeconds(changedAt));
            statement.setString(3, examId);
            return statement.executeUpdate();
        }
    }

    public void insertResult(Connection connection, String examId, String studentId, ExamResult result) throws SQLException {
        String sql = "INSERT INTO exam_result (exam_id, student_id, passed, score, recorded_at) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, examId);
            statement.setString(2, studentId);
            statement.setBoolean(3, result.isPassed());
            if (result.getScore() == null) {
                statement.setNull(4, Types.DECIMAL);     // score NULL khác với 0 (N18)
            } else {
                statement.setBigDecimal(4, result.getScore());
            }
            statement.setLong(5, DbTime.toEpochSeconds(result.getRecordedAt()));
            statement.executeUpdate();
        }
    }
}
