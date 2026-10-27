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

    private Status status;
    private LocalDateTime changedAt;

    protected Activity(String id, LocalDateTime start, int durationMinutes, Status status, LocalDateTime changedAt) {
        this.id = ModelChecks.id(id);
        this.start = ModelChecks.time(start);
        ModelChecks.require(durationMinutes > 0, "Duration must be positive!");
        this.durationMinutes = durationMinutes;
        ModelChecks.require(status != null, "Activity status is required!");
        this.status = status;
        this.changedAt = changedAt == null ? null : ModelChecks.time(changedAt);
        ModelChecks.require((status == Status.SCHEDULED) == (this.changedAt == null), "Invalid activity change time!");

        if (status == Status.COMPLETED) {
            ModelChecks.require(!this.changedAt.isBefore(getEnd()), "Completion precedes end!");
        }
        if (status == Status.CANCELLED) {
            ModelChecks.require(this.changedAt.isBefore(this.start), "Cancellation is at/after start!");
        }
    }

    protected Activity(Activity other) {
        this(other.id, other.start, other.durationMinutes, other.status, other.changedAt);
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

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void cancel(LocalDateTime now) {
        now = ModelChecks.time(now);

        if (status != Status.SCHEDULED || !now.isBefore(start)) {
            throw new IllegalStateException("Cannot cancel activity!");
        }
        status = Status.CANCELLED;
        changedAt = now;
    }

    public void complete(LocalDateTime now) {
        now = ModelChecks.time(now);

        if (status != Status.SCHEDULED || now.isBefore(getEnd())) {
            throw new IllegalStateException("Cannot complete activity!");
        }
        status = Status.COMPLETED;
        changedAt = now;
    }

    public boolean overlaps(LocalDateTime otherStart, int otherMinutes) {
        ModelChecks.require(otherMinutes > 0, "Duration must be positive!");
        return start.isBefore(otherStart.plusMinutes(otherMinutes)) && otherStart.isBefore(getEnd());
    }

    public abstract String getKind();

    public abstract Activity copy();

    @Override
    public String toString() {
        return id + " | " + getKind()
                + " | " + start + " -> " + getEnd()
                + " | " + status;
    }
}