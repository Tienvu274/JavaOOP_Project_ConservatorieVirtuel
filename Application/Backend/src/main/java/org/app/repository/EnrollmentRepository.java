package org.app.repository;

import org.app.database.DbTime;
import org.app.model.CoursePackage;
import org.app.model.Enrollment;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.app.model.FinitePackage;
import org.app.model.UnlimitedPackage;
import org.app.model.LessonParticipation;

import java.util.ArrayList;
import java.util.List;

public class EnrollmentRepository {

    public boolean studentExists(Connection connection, String studentId) throws SQLException {
        String sql = "SELECT id FROM student WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    public void insert(Connection connection, Enrollment enrollment) throws SQLException {
        String sql = "INSERT INTO enrollment "
                + "(id, student_id, package_id, "
                + "valid_from, valid_through, purchased_at, "
                + "package_name, price_cents, unlimited, lesson_count) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        CoursePackage pack = enrollment.getCoursePackage();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, enrollment.getId());
            statement.setString(2, enrollment.getStudentId());
            statement.setString(3, pack.getId());
            statement.setDate(4, Date.valueOf(enrollment.getValidFrom()));
            statement.setDate(5, Date.valueOf(enrollment.getValidUntil()));
            statement.setLong(6, DbTime.toEpochSeconds(enrollment.getPurchasedAt()));
            statement.setString(7, pack.getName());
            statement.setLong(8, pack.getPriceCents());
            statement.setBoolean(9, pack.isUnlimited());
            if (pack.isUnlimited()) {
                statement.setNull(10, Types.INTEGER);
            } else {
                statement.setInt(10, pack.getLessonLimit());
            }

            if (statement.executeUpdate() != 1) {
                throw new SQLException("Enrollment was not saved.");
            }
        }
    }

    public List<Enrollment> findByStudentId(Connection connection, String studentId) throws SQLException {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM enrollment "
                + "WHERE student_id = ? "
                + "ORDER BY purchased_at DESC, id";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    CoursePackage pack;
                    if (result.getBoolean("unlimited")) {
                        pack = new UnlimitedPackage(
                                result.getString("package_id"),
                                result.getString("package_name"),
                                result.getLong("price_cents")
                        );
                    } else {
                        pack = new FinitePackage(
                                result.getString("package_id"),
                                result.getString("package_name"),
                                result.getLong("price_cents"),
                                result.getInt("lesson_count")
                        );
                    }
                    Enrollment enrollment = new Enrollment(
                            result.getString("id"),
                            result.getString("student_id"),
                            pack,
                            result.getDate("valid_from").toLocalDate(),
                            result.getDate("valid_through").toLocalDate(),
                            DbTime.read(result, "purchased_at")
                    );
                    enrollments.add(enrollment);
                }
            }
        }
        return enrollments;
    }

    public List<LessonParticipation> findUsageByStudentId(Connection connection, String studentId) throws SQLException {
        List<LessonParticipation> history = new ArrayList<>();
        String sql = "SELECT * FROM lesson_participation "
                + "WHERE student_id = ? "
                + "ORDER BY reserved_at DESC, lesson_id";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    LessonParticipation participation = new LessonParticipation(
                            result.getString("lesson_id"),
                            result.getString("student_id"),
                            result.getString("enrollment_id"),
                            result.getInt("minutes"),
                            LessonParticipation.State.valueOf(result.getString("state")),
                            DbTime.read(result, "reserved_at"),
                            DbTime.read(result, "settled_at")
                            );
                    history.add(participation);
                }
            }
        }
        return history;
    }
}