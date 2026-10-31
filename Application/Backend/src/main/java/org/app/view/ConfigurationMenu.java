package org.app.view;

import org.app.database.*;
import org.app.model.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class ConfigurationMenu {
    public void show(Scanner input) {
        while (true) {
            System.out.println("\n===== CONFIGURATION / DATABASE =====");
            System.out.println("1. Test database connection");
            System.out.println("2. Show database information");
            System.out.println("3. Show business tables and row counts");
            System.out.println("4. List students");
            System.out.println("5. List teachers and availability");
            System.out.println("6. List rooms / instruments / packages");
            System.out.println("7. List enrollments and available minutes");
            System.out.println("8. List activities / exam results / charges / payments");
            System.out.println("9. Retry database initialization");
            System.out.println("0. Back to main menu");
            System.out.print("Choice: ");

            if (!input.hasNextLine()) return;
            String choice = input.nextLine().trim();

            try {
                switch (choice) {
                    case "0": return;
                    case "1": System.out.println(DbLoader.testConnection() ? "[OK] SELECT 1 succeeded." : "[ERROR] Connection test failed.");break;
                    case "2": System.out.println(DbLoader.databaseInfo());break;
                    case "3": ConsoleSupport.show(DbLoader.loadTableCounts());break;
                    case "4": showStudents();break;
                    case "5": showTeachers();break;
                    case "6": showResources();break;
                    case "7": showEnrollments();break;
                    case "8": showActivitiesAndBilling();break;
                    case "9": if (ConsoleSupport.initializeDatabase()) System.out.println("[OK] Database initialized.");break;
                    default:
                        System.out.println("Unknown choice. Enter 0-9.");
                }
            } catch (Exception error) {
                System.out.println("[ERROR] " + error.getMessage());
            }
        }
    }

    private void showStudents() throws Exception {
        for (Student student : DbLoader.loadStudents()) {
            System.out.println(student + " | " + student.getAddress()
                    + " | DOB=" + student.getBirthDate() + " | " + student.getPhone()
                    + " | " + student.getEmail() + " | " + student.getLevel()
                    + " | preferences=" + student.getPreferredInstruments());
        }
    }

    private void showTeachers() throws Exception {
        for (Teacher teacher : DbLoader.loadTeachers()) {
            System.out.println(teacher + " | rate=" + teacher.getHourlyRateCents()
                    + " cents/hour | " + teacher.getQualifications()
                    + " | " + teacher.getSpecializations());
            ConsoleSupport.show(teacher.getAvailability());
        }
    }

    private void showResources() throws Exception {
        System.out.println("\nROOMS");
        ConsoleSupport.show(DbLoader.loadRooms());

        System.out.println("\nINSTRUMENTS");
        ConsoleSupport.show(DbLoader.loadInstruments());

        System.out.println("\nPACKAGES");
        ConsoleSupport.show(DbLoader.loadPackages());
    }

    private void showEnrollments() throws Exception {
        List<LessonParticipation> history = DbLoader.loadParticipations();
        LocalDate today = LocalDate.now(DbTime.ZONE);

        for (Enrollment enrollment : DbLoader.loadEnrollments()) {
            Integer remaining = enrollment.availableMinutes(today, history);
            System.out.println(enrollment + " | available minutes=" + (remaining == null ? "UNLIMITED" : remaining));
        }

        System.out.println("\nUSAGE HISTORY");
        ConsoleSupport.show(history);
    }

    private void showActivitiesAndBilling() throws Exception {
        System.out.println("\nACTIVITIES");
        ConsoleSupport.show(DbLoader.loadActivities());

        System.out.println("\nEXAM REGISTRATIONS AND RESULTS");
        ConsoleSupport.show(DbLoader.loadExamRegistrations());

        System.out.println("\nCHARGES");
        ConsoleSupport.show(DbLoader.loadCharges());

        System.out.println("\nPAYMENTS");
        ConsoleSupport.show(DbLoader.loadPayments());
    }
}