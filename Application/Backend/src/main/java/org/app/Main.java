package org.app;

import org.app.controller.*;
import org.app.database.DbInitializer;
import org.app.database.DbLoader;
import org.app.database.DbTime;
import org.app.model.*;

import org.app.repository.*;
import org.app.service.*;
import org.app.view.*;

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

        StudentRepository studentRepository = new StudentRepository();
        StudentService studentService = new StudentService(studentRepository);
        StudentController studentController = new StudentController(studentService);
        StudentMenu studentMenu = new StudentMenu(studentController);

        TeacherRepository teacherRepository = new TeacherRepository();
        TeacherService teacherService = new TeacherService(teacherRepository);
        TeacherController teacherController = new TeacherController(teacherService);
        TeacherMenu teacherMenu = new TeacherMenu(teacherController);

        ResourceRepository resourceRepository = new ResourceRepository();
        ResourceService resourceService = new ResourceService(resourceRepository);
        ResourceController resourceController = new ResourceController(resourceService);
        ResourceMenu resourceMenu = new ResourceMenu(resourceController);


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
                //Trung
                System.out.println("30. List all students");
                System.out.println("31. Find student by ID");
                System.out.println("32. Create student");
                System.out.println("33. Update student");
                System.out.println("34. List teachers");
                System.out.println("35. Find teacher by ID");
                System.out.println("36. Create teacher");
                System.out.println("37. Update teacher");
                System.out.println("38. Add teacher specialization");
                System.out.println("39. Add teacher availability");
                System.out.println("40. List rooms");
                System.out.println("41. Find room by ID");
                System.out.println("42. Create room");
                System.out.println("43. Update room");
                System.out.println("44. List instruments");
                System.out.println("45. Find instrument by ID");
                System.out.println("46. Create instrument");
                System.out.println("47. Update instrument");

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
                            //Trung
                        case "30":
                            studentMenu.listStudents();
                            break;
                        case "31":
                            studentMenu.findStudent(input);
                            break;
                        case "32":
                            studentMenu.createStudent(input);
                            break;
                        case "33":
                            studentMenu.updateStudent(input);
                            break;
                        case "34": teacherMenu.listTeachers();
                            break;
                        case "35": teacherMenu.findTeacher(input);
                            break;
                        case "36": teacherMenu.createTeacher(input);
                            break;
                        case "37": teacherMenu.updateTeacher(input);
                            break;
                        case "38": teacherMenu.addSpecialization(input);
                            break;
                        case "39": teacherMenu.addAvailability(input);
                            break;
                        case "40": resourceMenu.listRooms();
                            break;
                        case "41": resourceMenu.findRoom(input);
                            break;
                        case "42": resourceMenu.createRoom(input);
                            break;
                        case "43": resourceMenu.updateRoom(input);
                            break;
                        case "44": resourceMenu.listInstruments();
                            break;
                        case "45": resourceMenu.findInstrument(input);
                            break;
                        case "46": resourceMenu.createInstrument(input);
                            break;
                        case "47": resourceMenu.updateInstrument(input);
                            break;

                        default:
                            System.out.println("Unknown choice. Enter 0-14.");

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