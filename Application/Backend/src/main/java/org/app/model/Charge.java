package org.app.model;

import java.time.LocalDateTime;

public class Charge implements Billable {

    private final String id;
    private final String studentId;

    private final String sourceType;
    private final String enrollmentId;
    private final String activityId;

    private final String description;
    private final long amountCents;
    private final LocalDateTime createdAt;

    public Charge(String id, String studentId, String sourceType, String enrollmentId, String activityId, String description, long amountCents, LocalDateTime createdAt) {
        this.id = ModelChecks.id(id);
        this.studentId = ModelChecks.id(studentId);
        ModelChecks.require(("PACKAGE".equals(sourceType) && enrollmentId != null && activityId == null)
                        || ("ACTIVITY".equals(sourceType) && activityId != null && enrollmentId == null),
                    "Invalid charge source!"
        );
        this.sourceType = sourceType;
        this.enrollmentId = enrollmentId == null ? null : ModelChecks.id(enrollmentId);
        this.activityId = activityId == null ? null : ModelChecks.id(activityId);
        this.description = ModelChecks.text(description, "Description");
        this.amountCents = ModelChecks.price(amountCents);
        this.createdAt = ModelChecks.time(createdAt);
    }

    public Charge(Charge other) {
        this(other.id, other.studentId, other.sourceType, other.enrollmentId, other.activityId, other.description, other.amountCents, other.createdAt);
    }

    public String getId() {
        return id;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getSourceType() {
        return sourceType;
    }

    public String getEnrollmentId() {
        return enrollmentId;
    }

    public String getActivityId() {
        return activityId;
    }

    public String getDescription() {
        return description;
    }

    public long getAmountCents() {
        return amountCents;
    }

    @Override
    public long getPriceCents() {
        return amountCents;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return id + " | " + studentId
                + " | " + amountCents + " cents"
                + " | " + description;
    }
}