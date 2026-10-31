package org.app.gui;

import org.app.controller.*;
import org.app.repository.*;
import org.app.service.*;

public class Backend {
    public final PackageController packages;
    public final EnrollmentController enrollments;
    public final SchedulingController scheduling;
    public final RentalController rentals;
    public final BillingController billing;
    public final StudentController students;
    public final TeacherController teachers;
    public final ResourceController resources;
    public final ExamController exams;

    public Backend() {
        PackageRepository packageRepo = new PackageRepository();
        ChargeRepository chargeRepo = new ChargeRepository();
        ResourceRepository resourceRepo = new ResourceRepository();
        SchedulingService schedulingService = new SchedulingService(new LessonRepository(), chargeRepo);

        packages = new PackageController(new PackageService(packageRepo));
        enrollments = new EnrollmentController(new EnrollmentService(new EnrollmentRepository(), packageRepo, chargeRepo));
        scheduling = new SchedulingController(schedulingService);
        rentals = new RentalController(new RentalService(new RentalRepository(), chargeRepo, schedulingService));
        billing = new BillingController(new BillingService(new PaymentRepository()));

        students = new StudentController(new StudentService(new StudentRepository()));
        teachers = new TeacherController(new TeacherService(new TeacherRepository()));
        resources = new ResourceController(new ResourceService(resourceRepo));
        exams = new ExamController(new ExamService(new ExamRepository(), resourceRepo));
    }
}