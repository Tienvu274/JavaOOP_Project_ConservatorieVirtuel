package org.app.model;

package com.conservatoire.model;

import java.time.LocalDate;

public class Enrollment {

    private final String id;
    private final String studentId;
    private final CoursePackage coursePackage;

    private final LocalDate validFrom;
    private final LocalDate validUntil;

    private int usedLessons;

    public Enrollment(String id, String studentId, CoursePackage coursePackage, LocalDate validFrom, LocalDate validUntil) {
        if (validUntil.isBefore(validFrom)) {
            throw new IllegalArgumentException("Invalid validity dates");
        }
        this.id = id;
        this.studentId = studentId;
        this.coursePackage = coursePackage.copy();
        this.validFrom = validFrom;
        this.validUntil = validUntil;
    }

    public Enrollment(Enrollment other) {
        this(other.id, other.studentId, other.coursePackage, other.validFrom, other.validUntil);
        this.usedLessons = other.usedLessons;
    }

    public boolean canUse(LocalDate day) {
        return !day.isBefore(validFrom)
                && !day.isAfter(validUntil)
                && (coursePackage.isUnlimited() || usedLessons < coursePackage.getLessonLimit());
    }

    public void useOneLesson(LocalDate day) {
        if (!canUse(day)) {
            throw new IllegalStateException("Package expired or empty");
        }
        usedLessons++;
    }

    public int remainingLessons() {
        if (coursePackage.isUnlimited()) {
            return Integer.MAX_VALUE;
        }
        return coursePackage.getLessonLimit() - usedLessons;
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

    @Override
    public String toString() {
        return id + " / "
                + studentId + " / "
                + coursePackage.getName()
                + " / remaining: "
                + (
                coursePackage.isUnlimited()
                        ? "unlimited"
                        : remainingLessons()
        );
    }
}