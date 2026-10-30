package org.app;

import org.app.controller.PackageController;
import org.app.database.DbInitializer;
import org.app.database.DbLoader;
import org.app.database.DbTime;
import org.app.model.*;

import org.app.view.ConsoleSupport;
import org.app.view.PackageMenu;
import org.app.view.EnrollmentMenu;
import org.app.view.SchedulingMenu;

import org.app.repository.PackageRepository;
import org.app.repository.LessonRepository;
import org.app.repository.EnrollmentRepository;
import org.app.repository.ChargeRepository;

import org.app.controller.EnrollmentController;
import org.app.controller.SchedulingController;
import org.app.controller.PackageController;

import org.app.service.EnrollmentService;
import org.app.service.PackageService;
import org.app.service.SchedulingService;

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

        EnrollmentRepository enrollmentRepository = new EnrollmentRepository();
        ChargeRepository chargeRepository = new ChargeRepository();
        EnrollmentService enrollmentService = new EnrollmentService(enrollmentRepository, packageRepository, chargeRepository);
        EnrollmentController enrollmentController = new EnrollmentController(enrollmentService);
        EnrollmentMenu enrollmentMenu = new EnrollmentMenu(enrollmentController);

        LessonRepository lessonRepository = new LessonRepository();

        SchedulingService schedulingService = new SchedulingService(lessonRepository, chargeRepository);
        SchedulingController schedulingController = new SchedulingController(schedulingService);
        SchedulingMenu schedulingMenu = new SchedulingMenu(schedulingController);

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
                System.out.println("12. Enroll student in a package");
                System.out.println("13. Student packages and learning hours");
                System.out.println("14. Student lesson usage history");
                System.out.println("15. Schedule a lesson");
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
                        case "12":
                            enrollmentMenu.enrollStudent(input);
                            break;
                        case "13":
                            enrollmentMenu.showStudentPackages(input);
                            break;
                        case "14":
                            enrollmentMenu.showUsageHistory(input);
                            break;
                        case "15":
                            schedulingMenu.scheduleLesson(input);
                            break;
                        default:
                            System.out.println("Unknown choice. Enter 0-15.");
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