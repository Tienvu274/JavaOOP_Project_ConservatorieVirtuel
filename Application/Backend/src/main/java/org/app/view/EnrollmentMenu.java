package org.app.view;

import org.app.controller.EnrollmentController;

import org.app.database.DbTime;

import org.app.model.Enrollment;
import org.app.model.EnrollmentSummary;
import org.app.model.LessonParticipation;

import java.util.List;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class EnrollmentMenu {

    private final EnrollmentController controller;

    public EnrollmentMenu(EnrollmentController controller) {
        this.controller = controller;
    }

    private String formatMinutes(Integer minutes) {
        if (minutes == null) {
            return "UNLIMITED";
        }
        return minutes + " min (" + (minutes / 60.0) + " h)";
    }

    public void enrollStudent(Scanner input) throws SQLException {

        String enrollmentId = readId(input, "Enrollment ID: ");
        String chargeId = readId(input, "Charge ID: ");
        String studentId = readId(input, "Student ID: ");
        String packageId = readId(input, "Package ID: ");
        LocalDate from = readDate(input, "Valid from (yyyy-MM-dd): ", null);
        LocalDate until = readDate(input, "Valid until (yyyy-MM-dd): ", from);
        Enrollment enrollment = controller.enrollStudent(
                enrollmentId,
                chargeId,
                studentId,
                packageId,
                from,
                until
        );
        System.out.println("Enrollment and charge saved.");
        System.out.println(enrollment);
        System.out.println("Charge ID: " + chargeId);
        System.out.println("Amount due: " + enrollment.getCoursePackage().getPriceCents() + " cents");
        System.out.println("No payment has been recorded.");
    }

    private String readId(Scanner input, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = input.nextLine().trim();
            if (value.matches("[A-Za-z0-9_-]{1,40}")) {
                return value;
            }
            System.out.println("Use 1-40 letters, digits, underscores or hyphens.");
        }
    }

    private LocalDate readDate(Scanner input, String prompt, LocalDate minimum) {
        while (true) {
            System.out.print(prompt);
            try {
                LocalDate value = LocalDate.parse(input.nextLine().trim());
                if (minimum != null && value.isBefore(minimum)) {
                    System.out.println("Date must be on or after " + minimum);
                    continue;
                }
                return value;
            } catch (DateTimeParseException error) {
                System.out.println("Enter a valid date, for example 2026-11-01.");
            }
        }
    }

    public void showStudentPackages(Scanner input) throws SQLException {
        String studentId = readId(input, "Student ID: ");
        LocalDate today = LocalDate.now(DbTime.ZONE);
        List<EnrollmentSummary> summaries = controller.getStudentPackageSummaries(studentId, today);
        System.out.println("\n===== STUDENT PACKAGES =====");
        System.out.println("Student: " + studentId);
        System.out.println("Validity checked on: " + today);
        if (summaries.isEmpty()) {
            System.out.println("This student has no package enrollments.");
            return;
        }
        for (EnrollmentSummary summary : summaries) {
            Enrollment enrollment = summary.getEnrollment();
            System.out.println("\nEnrollment: " + enrollment.getId());
            System.out.println("Package: " + enrollment.getCoursePackage().getName());
            System.out.println("Validity: " + enrollment.getValidFrom() + " -> " + enrollment.getValidUntil());
            System.out.println("Status: " + summary.getValidityStatus());
            System.out.println("Purchased: " + formatMinutes(summary.getPurchasedMinutes()));
            System.out.println("Consumed: " + formatMinutes(summary.getConsumedMinutes()));
            System.out.println("Reserved: " + formatMinutes(summary.getReservedMinutes()));
            System.out.println("Released (history): " + formatMinutes(summary.getReleasedMinutes()));
            System.out.println("Unallocated allowance: " + formatMinutes(summary.getRemainingMinutes()));
            System.out.println("Available for booking: " + formatMinutes(summary.getAvailableMinutes()));
        }
    }

    public void showUsageHistory(Scanner input) throws SQLException {
        String studentId = readId(input, "Student ID: ");
        List<LessonParticipation> history = controller.getUsageHistory(studentId);
        System.out.println("\n===== LESSON USAGE HISTORY =====");
        System.out.println("Student: " + studentId);
        if (history.isEmpty()) {
            System.out.println("No lesson usage history.");
            return;
        }
        for (LessonParticipation participation : history) {
            System.out.println("\nLesson: " + participation.getLessonId());
            System.out.println("Enrollment: "
                            + (participation.getEnrollmentId() == null
                            ? "SINGLE_PAID_LESSON"
                            : participation.getEnrollmentId())
            );
            System.out.println("Duration: " + formatMinutes(participation.getMinutes()));
            System.out.println("State: " + participation.getState());
            System.out.println("Reserved at: " + participation.getReservedAt());
            System.out.println("Settled at: "
                            + (participation.getSettledAt() == null
                            ? "NOT_SETTLED"
                            : participation.getSettledAt())
            );
        }
    }
}