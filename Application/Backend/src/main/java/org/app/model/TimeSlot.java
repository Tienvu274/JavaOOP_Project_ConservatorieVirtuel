package org.app.model;

import java.time.LocalDateTime;

public class TimeSlot {
    private final LocalDateTime start;
    private final LocalDateTime end;

    public TimeSlot(LocalDateTime start, LocalDateTime end) {
        this.start = ModelChecks.time(start);
        this.end = ModelChecks.time(end);
        ModelChecks.require(this.end.isAfter(this.start), "Availability end must follow start!");
    }

    public TimeSlot(TimeSlot other) {
        this(other.start, other.end);
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public boolean covers(LocalDateTime from, LocalDateTime through) {
        return !from.isBefore(start) && !through.isAfter(end);
    }

    @Override
    public String toString() {
        return start + " -> " + end;
    }
}