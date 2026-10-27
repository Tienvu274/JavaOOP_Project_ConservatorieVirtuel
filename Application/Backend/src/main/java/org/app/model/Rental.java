package org.app.model;

import java.time.LocalDateTime;

public class Rental extends Activity implements Billable {

    public enum ResourceType {ROOM, INSTRUMENT}

    private final String studentId;
    private final ResourceType resourceType;
    private final String resourceId;
    private final long priceCents;

    public Rental(String id, String studentId, ResourceType resourceType, String resourceId, LocalDateTime start, int durationMinutes, long priceCents, Status status, LocalDateTime changedAt) {
        super(id, start, durationMinutes, status, changedAt);
        this.studentId = ModelChecks.id(studentId);
        ModelChecks.require(resourceType != null, "Resource type is required!");
        this.resourceType = resourceType;
        this.resourceId = ModelChecks.id(resourceId);
        this.priceCents = ModelChecks.price(priceCents);
    }

    public Rental(Rental other) {
        this(other.getId(), other.studentId, other.resourceType, other.resourceId, other.getStart(), other.getDurationMinutes(), other.priceCents, other.getStatus(), other.getChangedAt());
    }

    public String getStudentId() {
        return studentId;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    @Override
    public long getPriceCents() {
        return priceCents;
    }

    @Override
    public String getKind() {
        return resourceType + "_RENTAL";
    }

    @Override
    public Activity copy() {
        return new Rental(this);
    }
}