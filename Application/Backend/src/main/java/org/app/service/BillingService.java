package org.app.service;

import org.app.database.DbContext;
import org.app.database.DbTime;
import org.app.model.Payment;
import org.app.repository.PaymentRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Locale;

public class BillingService {
    private final PaymentRepository repository;

    public BillingService(PaymentRepository repository) {
        this.repository = repository;
    }

    public long getBalanceCents(String studentId) throws SQLException {
        try (Connection connection = DbContext.open()) {
            requireStudent(connection, studentId);
            return repository.getBalanceCents(connection, studentId);
        }
    }

    public Payment recordPayment(String studentId, long amountCents, String method) throws SQLException {
        if (amountCents <= 0) {
            throw new IllegalArgumentException("Payment must be positive.");
        }
        if (method == null) {
            throw new IllegalArgumentException("Payment method is required.");
        }

        method = method.trim().toUpperCase(Locale.ROOT);

        if (!method.equals("CASH") && !method.equals("CARD") && !method.equals("TRANSFER")) {
            throw new IllegalArgumentException("Method must be CASH, CARD or TRANSFER.");
        }

        try (Connection connection = DbContext.open()) {
            connection.setAutoCommit(false);
            try {
                requireStudent(connection, studentId);
                long balance = repository.getBalanceCents(connection, studentId);
                if (amountCents > balance) {
                    throw new IllegalArgumentException("Payment exceeds outstanding balance: " + balance + " cents.");
                }
                Payment payment = new Payment(repository.nextId(connection), studentId, amountCents, method, LocalDateTime.now(DbTime.ZONE).withNano(0));
                repository.insert(connection, payment);
                connection.commit();
                return payment;
            } catch (SQLException | RuntimeException error) {
                try {
                    connection.rollback();
                }
                catch (SQLException rollbackError) {
                    error.addSuppressed(rollbackError);
                }
                throw error;
            }
        }
    }

    private void requireStudent(Connection connection, String studentId) throws SQLException {
        if (studentId == null || !studentId.matches("[A-Za-z0-9_-]{1,40}")) {
            throw new IllegalArgumentException("Invalid student ID.");
        }

        if (!repository.studentExists(connection, studentId)) {
            throw new IllegalArgumentException("Student not found: " + studentId);
        }
    }
}