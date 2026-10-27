package org.app;

import org.app.database.DbInitializer;
import org.app.database.DbLoader;
import org.app.database.DbTime;
import org.app.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static void initialize() {
        try {
            DbInitializer.initialize();
        } catch (Exception error) {
            System.out.println("[DB] Initialization failed: " + error.getMessage());
            System.out.println("Check MySQL Server and "
                            + "DB_HOST/DB_PORT/DB_NAME/"
                            + "DB_USER/DB_PASSWORD.");
            System.out.println("Correct the configuration and restart, "
                            + "or retry with option 9.");
        }
    }

    private static void show(List<?> values) {
        if (values.isEmpty()) {
            System.out.println("No records.");
        }
        for (Object value : values) {
            System.out.println(value);
        }
    }

    public static void main(String[] args) {
        initialize();
        try (Scanner input = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n===== CONSERVATOIRE VIRTUEL - SKELETON =====");
                System.out.println("1. Test database connection");
                System.out.println("2. Show database information");
                System.out.println("3. Show 16 business tables and row counts");
                System.out.println("4. List students");
                System.out.println("5. List teachers and availability");
                System.out.println("6. List rooms / instruments / packages");
                System.out.println("7. List enrollments and available minutes");
                System.out.println("8. List activities / exam results / charges / payments");
                System.out.println("9. Retry database initialization");
                System.out.println("0. Exit");
                System.out.print("Choice: ");

                if (!input.hasNextLine()) {
                    return;
                }
                String choice = input.nextLine().trim();

                try {
                    switch (choice) {
                        case "0":
                            return;
                        case "1":
                            System.out.println(DbLoader.testConnection() ? "[OK] SELECT 1 succeeded." : "[ERROR] Connection test failed.");
                            break;
                        case "2":
                            System.out.println(DbLoader.databaseInfo());
                            break;
                        case "3":
                            show(DbLoader.loadTableCounts());
                            break;
                        case "4":
                            for (Student student : DbLoader.loadStudents()) {
                                System.out.println(
                                        student
                                                + " | " + student.getAddress()
                                                + " | DOB=" + student.getBirthDate()
                                                + " | " + student.getPhone()
                                                + " | " + student.getEmail()
                                                + " | " + student.getLevel()
                                                + " | preferences="
                                                + student.getPreferredInstruments()
                                );
                            }
                            break;
                        case "5":
                            for (Teacher teacher : DbLoader.loadTeachers()) {
                                System.out.println(
                                        teacher
                                                + " | rate="
                                                + teacher.getHourlyRateCents()
                                                + " cents/hour"
                                                + " | "
                                                + teacher.getQualifications()
                                                + " | "
                                                + teacher.getSpecializations()
                                );
                                show(teacher.getAvailability());
                            }
                            break;
                        case "6":
                            show(DbLoader.loadRooms());
                            show(DbLoader.loadInstruments());
                            show(DbLoader.loadPackages());
                            break;
                        case "7":
                            List<LessonParticipation> history = DbLoader.loadParticipations();
                            for (Enrollment enrollment : DbLoader.loadEnrollments()) {
                                Integer remaining = enrollment.availableMinutes(LocalDate.now(DbTime.ZONE), history);
                                System.out.println(enrollment + " | available minutes=" + (remaining == null ? "UNLIMITED" : remaining));
                            }
                            show(history);
                            break;
                        case "8":
                            show(DbLoader.loadActivities());
                            show(DbLoader.loadExamRegistrations());
                            show(DbLoader.loadCharges());
                            show(DbLoader.loadPayments());
                            break;
                        case "9":
                            initialize();
                            break;
                        default:
                            System.out.println("Unknown choice. Enter 0-9.");
                    }
                } catch (Exception error) {
                    System.out.println("[ERROR] " + error.getMessage());
                }
            }
        }
    }
}