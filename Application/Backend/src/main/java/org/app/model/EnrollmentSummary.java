package org.app.model;

import java.time.LocalDate;
import java.util.List;

public class EnrollmentSummary {

    private final Enrollment enrollment;
    private final LocalDate referenceDate;

    private final Integer remainingMinutes;
    private final Integer availableMinutes;

    private int consumedMinutes;
    private int reservedMinutes;
    private int releasedMinutes;

    public EnrollmentSummary(Enrollment enrollment, List<LessonParticipation> history, LocalDate referenceDate) {
        this.enrollment = new Enrollment(enrollment);
        this.referenceDate = referenceDate;
        for (LessonParticipation participation : history) {
            if (!enrollment.getId().equals(participation.getEnrollmentId())) {
                continue;
            }
            switch (participation.getState()) {
                case CONSUMED:
                    consumedMinutes += participation.getMinutes();
                    break;
                case RESERVED:
                    reservedMinutes += participation.getMinutes();
                    break;
                case RELEASED:
                    releasedMinutes += participation.getMinutes();
                    break;
            }
        }
        remainingMinutes = enrollment.remainingMinutes(history);
        availableMinutes = enrollment.availableMinutes(referenceDate, history);
    }

    public Enrollment getEnrollment() {
        return new Enrollment(enrollment);
    }

    public Integer getPurchasedMinutes() {
        CoursePackage pack = enrollment.getCoursePackage();
        if (pack.isUnlimited()) {
            return null;
        }
        return pack.getLessonLimit() * 60;
    }

    public int getConsumedMinutes() {
        return consumedMinutes;
    }

    public int getReservedMinutes() {
        return reservedMinutes;
    }

    public int getReleasedMinutes() {
        return releasedMinutes;
    }

    public Integer getRemainingMinutes() {
        return remainingMinutes;
    }

    public Integer getAvailableMinutes() {
        return availableMinutes;
    }

    public String getValidityStatus() {
        if (referenceDate.isBefore(enrollment.getValidFrom())) {
            return "NOT_STARTED";
        }

        if (referenceDate.isAfter(enrollment.getValidUntil())) {
            return "EXPIRED";
        }

        return "ACTIVE";
    }
}