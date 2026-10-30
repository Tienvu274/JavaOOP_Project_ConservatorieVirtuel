package org.app.controller;

import org.app.model.Payment;
import org.app.service.BillingService;

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
}