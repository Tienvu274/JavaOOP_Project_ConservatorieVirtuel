package org.app.controller;

import org.app.model.Payment;
import org.app.service.BillingService;
import org.app.model.Charge;

import java.util.List;
import java.time.YearMonth;
import java.sql.SQLException;

public class BillingController {

    private final BillingService service;

    public BillingController(BillingService service) {
        this.service = service;
    }

    public long getBalanceCents(String studentId) throws SQLException {
        return service.getBalanceCents(studentId);
    }

    public Payment recordPayment(String studentId, long amountCents, String method) throws SQLException {
        return service.recordPayment(studentId, amountCents, method);
    }

    public List<Charge> getStudentCharges(String studentId) throws SQLException {
        return service.getStudentCharges(studentId);
    }

    public List<Payment> getStudentPayments(String studentId) throws SQLException {
        return service.getStudentPayments(studentId);
    }

    public long getTotalChargedCents(String studentId) throws SQLException {
        return service.getTotalChargedCents(studentId);
    }

    public long getTotalPaidCents(String studentId) throws SQLException {
        return service.getTotalPaidCents(studentId);
    }

    public long getMonthlyChargedCents(YearMonth month) throws SQLException {
        return service.getMonthlyChargedCents(month);
    }

    public long getMonthlyPaidCents(YearMonth month) throws SQLException {
        return service.getMonthlyPaidCents(month);
    }
}