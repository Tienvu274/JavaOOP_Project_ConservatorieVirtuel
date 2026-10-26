package org.app.model;

import java.time.LocalDateTime;

public abstract class Activity implements Schedulable {

    public enum Status {
        SCHEDULED,
        COMPLETED,
        CANCELLED
    }

    private final String id;
    private final LocalDateTime start;
    private final int durationMinutes;

    private Status status = Status.SCHEDULED;

    protected Activity(
            String id,
            LocalDateTime start,
            int durationMinutes) {

        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        this.id = id;
        this.start = start;
        this.durationMinutes = durationMinutes;
    }

    public String getId() {
        return id;
    }

    @Override
    public LocalDateTime getStart() {
        return start;
    }

    @Override
    public LocalDateTime getEnd() {
        return start.plusMinutes(durationMinutes);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public abstract String getKind();

    public boolean overlaps(LocalDateTime otherStart, int otherMinutes) {
        LocalDateTime otherEnd = otherStart.plusMinutes(otherMinutes);
        return start.isBefore(otherEnd) && otherStart.isBefore(getEnd());
    }

    @Override
    public String toString() {
        return id + " / "
                + getKind() + " / "
                + start + " / "
                + status;
    }
}