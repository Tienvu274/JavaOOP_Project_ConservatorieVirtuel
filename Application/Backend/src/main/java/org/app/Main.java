package org.app;


import org.app.controller.*;
import org.app.repository.*;
import org.app.service.*;
import org.app.view.*;
import org.app.database.*;

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

        RentalRepository rentalRepository = new RentalRepository();
        RentalService rentalService = new RentalService(rentalRepository, chargeRepository, schedulingService);
        RentalController rentalController = new RentalController(rentalService);
        RentalMenu rentalMenu = new RentalMenu(rentalController);

        PaymentRepository paymentRepository = new PaymentRepository();
        BillingService billingService = new BillingService(paymentRepository);
        BillingController billingController = new BillingController(billingService);
        BillingMenu billingMenu = new BillingMenu(billingController);

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

        ExamRepository examRepository = new ExamRepository();
        ExamService examService = new ExamService(examRepository, resourceRepository);
        ExamController examController = new ExamController(examService);
        ExamMenu examMenu = new ExamMenu(examController);

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
                System.out.println("16. Cancel a lesson");
                System.out.println("17. Complete a lesson");
                System.out.println("18. View teacher schedule");
                System.out.println("19. View student schedule and activities");
                System.out.println("20. Book a room");
                System.out.println("21. Rent an instrument");
                System.out.println("22. Complete a rental");
                System.out.println("23. Cancel a rental");
                System.out.println("24. Record a payment");
                System.out.println("25. View student balance");
                System.out.println("26. View student billing history and totals");
                System.out.println("27. View monthly billing summary");
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
                System.out.println("48. List exams");
                System.out.println("49. Find exam by ID");
                System.out.println("50. Create exam");
                System.out.println("51. Check exam conflicts");
                System.out.println("52. Register student to exam");
                System.out.println("53. Show exam registrations");
                System.out.println("54. Complete exam");
                System.out.println("55. Record exam result");
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
                        case "16":
                            schedulingMenu.cancelLesson(input);
                            break;
                        case "17":
                            schedulingMenu.completeLesson(input);
                            break;
                        case "18":
                            schedulingMenu.showTeacherSchedule(input);
                            break;

                        case "19":
                            schedulingMenu.showStudentSchedule(input);
                            break;
                        case "20":
                            rentalMenu.bookRoom(input);
                            break;

                        case "21":
                            rentalMenu.rentInstrument(input);
                            break;
                        case "22":
                            rentalMenu.completeRental(input);
                            break;

                        case "23":
                            rentalMenu.cancelRental(input);
                            break;
                        case "24":
                            billingMenu.recordPayment(input);
                            break;

                        case "25":
                            billingMenu.showBalance(input);
                            break;
                        case "26":
                            billingMenu.showStudentBilling(input);
                            break;

                        case "27":
                            billingMenu.showMonthlyBilling(input);
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
                        case "48": examMenu.listExams();
                            break;
                        case "49": examMenu.findExam(input);
                            break;
                        case "50": examMenu.createExam(input);
                            break;
                        case "51": examMenu.checkConflicts(input);
                            break;
                        case "52": examMenu.registerStudent(input);
                            break;
                        case "53": examMenu.showRegistrations(input);
                            break;
                        case "54": examMenu.completeExam(input);
                            break;
                        case "55": examMenu.recordResult(input);
                            break;
                        default:
                            System.out.println("Unknown choice. Enter 0-55.");
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