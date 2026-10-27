package org.app.model;

import java.time.LocalDateTime;

public class Payment {

    private final String id;
    private final String studentId;
    private final long amountCents;
    private final String method;
    private final LocalDateTime paidAt;

    public Payment(String id, String studentId, long amountCents, String method, LocalDateTime paidAt) {
        this.id = ModelChecks.id(id);
        this.studentId = ModelChecks.id(studentId);
        ModelChecks.require(amountCents > 0, "Payment must be positive!");
        this.amountCents = amountCents;
        this.method = ModelChecks.text(method, "Payment method");
        this.paidAt = ModelChecks.time(paidAt);
    }

    public Payment(Payment other) {
        this(other.id, other.studentId, other.amountCents, other.method, other.paidAt);
    }

    public String getId() {
        return id;
    }

    public String getStudentId() {
        return studentId;
    }

    public long getAmountCents() {
        return amountCents;
    }

    public String getMethod() {
        return method;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    @Override
    public String toString() {
        return id + " | " + studentId
                + " | " + amountCents + " cents"
                + " | " + method;
    }
}