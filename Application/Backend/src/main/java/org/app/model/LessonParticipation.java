package org.app.model;

import java.time.LocalDateTime;

public class LessonParticipation {

    public enum State {RESERVED, CONSUMED, RELEASED}

    private final String lessonId;
    private final String studentId;
    private final String enrollmentId;
    private final int minutes;
    private final State state;
    private final LocalDateTime reservedAt;
    private final LocalDateTime settledAt;

    public LessonParticipation(String lessonId, String studentId, String enrollmentId, int minutes, State state, LocalDateTime reservedAt, LocalDateTime settledAt) {
        this.lessonId = ModelChecks.id(lessonId);
        this.studentId = ModelChecks.id(studentId);
        this.enrollmentId = enrollmentId == null ? null : ModelChecks.id(enrollmentId);
        ModelChecks.require(minutes == 60, "Lesson usage must be 60 minutes");
        ModelChecks.require(state != null, "Usage state is required");
        this.minutes = minutes;
        this.state = state;
        this.reservedAt = ModelChecks.time(reservedAt);
        this.settledAt = settledAt == null ? null : ModelChecks.time(settledAt);
        ModelChecks.require((state == State.RESERVED) == (this.settledAt == null), "Invalid settlement time!");
        ModelChecks.require(this.settledAt == null || !this.settledAt.isBefore(this.reservedAt), "Settlement precedes booking!");
    }

    public LessonParticipation(LessonParticipation other) {
        this(other.lessonId, other.studentId, other.enrollmentId, other.minutes, other.state, other.reservedAt, other.settledAt);
    }

    public LessonParticipation settle(State target, LocalDateTime at) {
        if (state != State.RESERVED || target == State.RESERVED) {
            throw new IllegalStateException("Usage cannot be settled again!");
        }
        return new LessonParticipation(lessonId, studentId, enrollmentId, minutes, target, reservedAt, at);
    }

    public String getLessonId() {
        return lessonId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getEnrollmentId() {
        return enrollmentId;
    }

    public int getMinutes() {
        return minutes;
    }

    public State getState() {
        return state;
    }

    public LocalDateTime getReservedAt() {
        return reservedAt;
    }

    public LocalDateTime getSettledAt() {
        return settledAt;
    }

    @Override
    public String toString() {
        return lessonId + "/" + studentId + " | " + minutes + " min" + " | " + state;
    }
}