package org.app.repository;

import org.app.database.DbTime;
import org.app.model.Payment;

import java.sql.*;

public class PaymentRepository {

    public boolean studentExists(Connection connection, String studentId) throws SQLException {
        String sql = "SELECT id FROM student WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    public long getBalanceCents(Connection connection, String studentId) throws SQLException {
        String sql = "SELECT COALESCE((SELECT SUM(amount_cents) FROM charge WHERE student_id = ?), 0) "
                + "- COALESCE((SELECT SUM(amount_cents) FROM payment WHERE student_id = ?), 0)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            statement.setString(2, studentId);
            try (ResultSet result = statement.executeQuery()) {
                result.next();
                return result.getLong(1);
            }
        }
    }

    public String nextId(Connection connection) throws SQLException {
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(id, 5) AS UNSIGNED)), 0) + 1 "
                + "FROM payment WHERE id REGEXP '^PAY_[0-9]+$'";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            result.next();
            return "PAY_" + String.format(java.util.Locale.ROOT, "%03d", result.getLong(1));
        }
    }

    public void insert(Connection connection, Payment payment) throws SQLException {
        String sql = "INSERT INTO payment (id, student_id, amount_cents, method, paid_at) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, payment.getId());
            statement.setString(2, payment.getStudentId());
            statement.setLong(3, payment.getAmountCents());
            statement.setString(4, payment.getMethod());
            statement.setLong(5, DbTime.toEpochSeconds(payment.getPaidAt()));

            if (statement.executeUpdate() != 1) {
                throw new SQLException("Payment was not saved.");
            }
        }
    }
}