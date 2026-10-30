package org.app.service;

import org.app.database.DbContext;
import org.app.database.DbTime;
import org.app.model.Payment;
import org.app.repository.PaymentRepository;
import org.app.model.Charge;

import java.util.List;
import java.time.YearMonth;
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

    public List<Charge> getStudentCharges(String studentId) throws SQLException {
        try (Connection connection = DbContext.open()) {
            requireStudent(connection, studentId);
            return repository.findChargesByStudentId(connection, studentId);
        }
    }

    public List<Payment> getStudentPayments(String studentId) throws SQLException {
        try (Connection connection = DbContext.open()) {
            requireStudent(connection, studentId);
            return repository.findPaymentsByStudentId(connection, studentId);
        }
    }

    public long getTotalChargedCents(String studentId) throws SQLException {
        long total = 0;
        for (Charge charge : getStudentCharges(studentId)) {
            total = Math.addExact(total, charge.getAmountCents());
        }
        return total;
    }

    public long getTotalPaidCents(String studentId) throws SQLException {
        long total = 0;
        for (Payment payment : getStudentPayments(studentId)) {
            total = Math.addExact(total, payment.getAmountCents());
        }
        return total;
    }

    public long getMonthlyChargedCents(YearMonth month) throws SQLException {
        return getMonthlyTotal(month, false);
    }

    public long getMonthlyPaidCents(YearMonth month) throws SQLException {
        return getMonthlyTotal(month, true);
    }

    private long getMonthlyTotal(YearMonth month, boolean payments) throws SQLException {
        if (month == null) throw new IllegalArgumentException("Month is required.");
        long from = DbTime.toEpochSeconds(month.atDay(1).atStartOfDay());
        long until = DbTime.toEpochSeconds(month.plusMonths(1).atDay(1).atStartOfDay());
        try (Connection connection = DbContext.open()) {
            if (payments) return repository.getMonthlyPaidCents(connection, from, until);
            return repository.getMonthlyChargedCents(connection, from, until);
        }
    }
}