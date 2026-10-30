package org.app.repository;

import org.app.database.DbTime;
import org.app.model.Payment;
import org.app.model.Charge;

import java.util.List;
import java.util.ArrayList;
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

    public List<Charge> findChargesByStudentId(Connection connection, String studentId) throws SQLException {
        List<Charge> charges = new ArrayList<>();
        String sql = "SELECT * FROM charge WHERE student_id = ? ORDER BY created_at, id";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    charges.add(new Charge(result.getString("id"), result.getString("student_id"), result.getString("source_type"), result.getString("enrollment_id"), result.getString("activity_id"), result.getString("description"), result.getLong("amount_cents"), DbTime.read(result, "created_at")));
                }
            }
        }
        return charges;
    }

    public List<Payment> findPaymentsByStudentId(Connection connection, String studentId) throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payment WHERE student_id = ? ORDER BY paid_at, id";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    payments.add(new Payment(result.getString("id"), result.getString("student_id"), result.getLong("amount_cents"), result.getString("method"), DbTime.read(result, "paid_at")));
                }
            }
        }
        return payments;
    }

    public long getMonthlyChargedCents(Connection connection, long from, long until) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount_cents), 0) FROM charge WHERE created_at >= ? AND created_at < ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, from);
            statement.setLong(2, until);
            try (ResultSet result = statement.executeQuery()) {
                result.next();
                return result.getLong(1);
            }
        }
    }

    public long getMonthlyPaidCents(Connection connection, long from, long until) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount_cents), 0) FROM payment WHERE paid_at >= ? AND paid_at < ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, from);
            statement.setLong(2, until);
            try (ResultSet result = statement.executeQuery()) {
                result.next();
                return result.getLong(1);
            }
        }
    }
}