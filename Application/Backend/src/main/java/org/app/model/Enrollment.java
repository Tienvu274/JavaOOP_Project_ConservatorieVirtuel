package org.app.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Enrollment {

    private final String id;
    private final String studentId;
    private final CoursePackage coursePackage;
    private final LocalDate validFrom;
    private final LocalDate validUntil;
    private final LocalDateTime purchasedAt;

    public Enrollment(String id, String studentId, CoursePackage coursePackage, LocalDate validFrom, LocalDate validUntil, LocalDateTime purchasedAt) {
        this.id = ModelChecks.id(id);
        this.studentId = ModelChecks.id(studentId);
        this.coursePackage = Objects.requireNonNull(coursePackage, "Package is required!").copy();
        this.validFrom = Objects.requireNonNull(validFrom, "Start date is required!");
        this.validUntil = Objects.requireNonNull(validUntil, "End date is required!");
        ModelChecks.require(!validUntil.isBefore(validFrom), "Invalid validity dates!");
        this.purchasedAt = ModelChecks.time(purchasedAt);
    }

    public Enrollment(Enrollment other) {
        this(other.id, other.studentId, other.coursePackage, other.validFrom, other.validUntil, other.purchasedAt);
    }

    public boolean isValidOn(LocalDate day) {
        return !day.isBefore(validFrom) && !day.isAfter(validUntil);
    }

    public Integer remainingMinutes(List<LessonParticipation> history) {
        if (coursePackage.isUnlimited()) {
            return null;
        }
        int used = 0;
        for (LessonParticipation participation : history) {
            if (id.equals(participation.getEnrollmentId())) {
                ModelChecks.require(studentId.equals(participation.getStudentId()), "Usage belongs to another student!");
                if (participation.getState() != LessonParticipation.State.RELEASED) {
                    used = Math.addExact(used, participation.getMinutes());
                }
            }
        }
        int remaining = coursePackage.getLessonLimit() * 60 - used;
        ModelChecks.require(remaining >= 0, "Package usage exceeds allowance!");
        return remaining;
    }

    public Integer availableMinutes(LocalDate day, List<LessonParticipation> history) {
        if (!isValidOn(day)) {
            return 0;
        }
        return remainingMinutes(history);
    }

    public String getId() {
        return id;
    }

    public String getStudentId() {
        return studentId;
    }

    public CoursePackage getCoursePackage() {
        return coursePackage.copy();
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public LocalDateTime getPurchasedAt() {
        return purchasedAt;
    }

    @Override
    public String toString() {
        return id + " / " + studentId
                + " / " + coursePackage.getName()
                + " / " + validFrom + " -> " + validUntil;
    }
}