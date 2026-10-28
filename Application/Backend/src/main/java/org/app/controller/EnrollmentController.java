package org.app.controller;

import org.app.model.Enrollment;
import org.app.model.EnrollmentSummary;
import org.app.model.LessonParticipation;

import org.app.service.EnrollmentService;

import java.util.List;
import java.sql.SQLException;
import java.time.LocalDate;

public class EnrollmentController {
    private final EnrollmentService service;

    public EnrollmentController(EnrollmentService service) {
        this.service = service;
    }

    public Enrollment enrollStudent(String enrollmentId, String chargeId, String studentId, String packageId, LocalDate validFrom, LocalDate validUntil) throws SQLException {
        return service.enrollStudent(
                enrollmentId,
                chargeId,
                studentId,
                packageId,
                validFrom,
                validUntil
        );
    }

    public List<EnrollmentSummary> getStudentPackageSummaries(String studentId, LocalDate day) throws SQLException {
        return service.getStudentPackageSummaries(studentId, day);
    }

    public List<LessonParticipation> getUsageHistory(String studentId) throws SQLException {
        return service.getUsageHistory(studentId);
    }
}