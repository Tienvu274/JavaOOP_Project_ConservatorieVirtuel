package org.app;

import org.app.controller.PackageController;
import org.app.database.DbInitializer;
import org.app.database.DbLoader;
import org.app.database.DbTime;
import org.app.model.*;

import org.app.repository.PackageRepository;
import org.app.service.PackageService;
import org.app.view.PackageMenu;
import org.app.view.ConsoleSupport;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;


public class Main {

    public static void main(String[] args) {
        if (!ConsoleSupport.initializeDatabase()) {
            return;
        }

        PackageRepository packageRepository = new PackageRepository();
        PackageService packageService = new PackageService(packageRepository);
        PackageController packageController = new PackageController(packageService);
        PackageMenu packageMenu = new PackageMenu(packageController);

        try (Scanner input = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n===== CONSERVATOIRE VIRTUEL =====");
                System.out.println("1. Test database connection");
                System.out.println("2. Show database information");
                System.out.println("3. Show 16 business tables and row counts");
                System.out.println("4. List students");
                System.out.println("5. List teachers and availability");
                System.out.println("6. List rooms / instruments / packages");
                System.out.println("7. List enrollments and available minutes");
                System.out.println("8. List activities / exam results / charges / payments");
                System.out.println("9. Retry database initialization");
                System.out.println("10. Create course package");
                System.out.println("11. List course packages");
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
                            ConsoleSupport.show(DbLoader.loadTableCounts());
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
                                ConsoleSupport.show(teacher.getAvailability());
                            }
                            break;
                        case "6":
                            ConsoleSupport.show(DbLoader.loadRooms());
                            ConsoleSupport.show(DbLoader.loadInstruments());
                            ConsoleSupport.show(DbLoader.loadPackages());
                            break;
                        case "7":
                            List<LessonParticipation> history = DbLoader.loadParticipations();
                            for (Enrollment enrollment : DbLoader.loadEnrollments()) {
                                Integer remaining = enrollment.availableMinutes(LocalDate.now(DbTime.ZONE), history);
                                System.out.println(enrollment + " | available minutes=" + (remaining == null ? "UNLIMITED" : remaining));
                            }
                            ConsoleSupport.show(history);
                            break;
                        case "8":
                            ConsoleSupport.show(DbLoader.loadActivities());
                            ConsoleSupport.show(DbLoader.loadExamRegistrations());
                            ConsoleSupport.show(DbLoader.loadCharges());
                            ConsoleSupport.show(DbLoader.loadPayments());
                            break;
                        case "9":
                            if (!ConsoleSupport.initializeDatabase()) {
                                return;
                            }
                            break;
                        case "10":
                            packageMenu.createPackage(input);
                            break;

                        case "11":
                            packageMenu.listPackages();
                            break;
                        default:
                            System.out.println("Unknown choice. Enter 0-11.");
                    }
                } catch (NumberFormatException error) {
                    System.out.println("[ERROR] Please enter a valid number.");
                } catch (Exception error) {
                    System.out.println("[ERROR] " + error.getMessage());
                }
            }
        }
    }
}