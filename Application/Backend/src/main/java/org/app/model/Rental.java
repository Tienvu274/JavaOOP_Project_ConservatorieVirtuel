package org.app.model;

import java.time.LocalDateTime;

public class Rental extends Activity implements Billable {

    public enum ResourceType {
        ROOM,
        INSTRUMENT
    }

    private final String studentId;
    private final ResourceType resourceType;
    private final String resourceId;
    private final long priceCents;

    public Rental(String id, String studentId, ResourceType resourceType, String resourceId, LocalDateTime start, int durationMinutes, long priceCents) {
        super(id, start, durationMinutes);
        this.studentId = studentId;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.priceCents = priceCents;
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
}
