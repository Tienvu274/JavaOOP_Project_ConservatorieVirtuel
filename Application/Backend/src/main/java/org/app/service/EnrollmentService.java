package org.app.service;

import org.app.database.DbContext;
import org.app.database.DbTime;
import org.app.model.Charge;
import org.app.model.CoursePackage;
import org.app.model.Enrollment;
import org.app.repository.ChargeRepository;
import org.app.repository.EnrollmentRepository;
import org.app.repository.PackageRepository;

import org.app.model.EnrollmentSummary;
import org.app.model.LessonParticipation;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final PackageRepository packageRepository;
    private final ChargeRepository chargeRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository, PackageRepository packageRepository, ChargeRepository chargeRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.packageRepository = packageRepository;
        this.chargeRepository = chargeRepository;
    }

    public Enrollment enrollStudent(String enrollmentId, String chargeId, String studentId, String packageId, LocalDate validFrom, LocalDate validUntil) throws SQLException {
        try (Connection connection = DbContext.open()) {
            connection.setAutoCommit(false);
            try {
                if (!enrollmentRepository.studentExists(connection, studentId)) {
                    throw new IllegalArgumentException("Student not found: " + studentId);
                }
                CoursePackage pack = packageRepository.findById(connection, packageId);
                if (pack == null) {
                    throw new IllegalArgumentException("Package not found: " + packageId);
                }

                LocalDateTime now = LocalDateTime.now(DbTime.ZONE).withNano(0);
                Enrollment enrollment = new Enrollment(
                        enrollmentId,
                        studentId,
                        pack,
                        validFrom,
                        validUntil,
                        now
                );
                Charge charge = new Charge(
                        chargeId,
                        studentId,
                        "PACKAGE",
                        enrollmentId,
                        null,
                        "Course package: " + pack.getName(),
                        pack.getPriceCents(),
                        now
                );

                enrollmentRepository.insert(connection, enrollment);
                chargeRepository.insert(connection, charge);
                connection.commit();
                return enrollment;

            } catch (SQLException | RuntimeException error) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackError) {
                    error.addSuppressed(rollbackError);
                }

                if (error instanceof SQLException && ((SQLException) error).getErrorCode() == 1062) {
                    throw new IllegalArgumentException("Enrollment ID or charge ID already exists.", error);
                }
                throw error;
            }
        }
    }

    public List<EnrollmentSummary> getStudentPackageSummaries(String studentId, LocalDate day) throws SQLException {
        if (day == null) {
            throw new IllegalArgumentException("Reference date is required.");
        }

        try (Connection connection = DbContext.open()) {
            requireStudent(connection, studentId);
            List<Enrollment> enrollments = enrollmentRepository.findByStudentId(connection, studentId);
            List<LessonParticipation> history = enrollmentRepository.findUsageByStudentId(connection, studentId);
            List<EnrollmentSummary> summaries = new ArrayList<>();
            for (Enrollment enrollment : enrollments) {
                summaries.add(
                        new EnrollmentSummary(
                                enrollment,
                                history,
                                day
                        )
                );
            }
            return summaries;
        }
    }

    public List<LessonParticipation> getUsageHistory(String studentId) throws SQLException {
        try (Connection connection = DbContext.open()) {
            requireStudent(connection, studentId);
            return enrollmentRepository.findUsageByStudentId(connection, studentId);
        }
    }

    private void requireStudent(Connection connection, String studentId) throws SQLException {
        if (studentId == null || !studentId.matches("[A-Za-z0-9_-]{1,40}")) {
            throw new IllegalArgumentException("Invalid student ID.");
        }
        if (!enrollmentRepository.studentExists(connection, studentId)) {
            throw new IllegalArgumentException("Student not found: " + studentId);
        }
    }
}