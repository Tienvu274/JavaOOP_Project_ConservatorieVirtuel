package org.app.view;

import org.app.controller.TeacherController;
import org.app.model.Teacher;
import org.app.model.TimeSlot;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class TeacherMenu {

    private final TeacherController controller;
    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public TeacherMenu(TeacherController controller) {
        this.controller = controller;
    }

    //T03
    public void listTeachers() throws SQLException {
        List<Teacher> teachers = controller.getAllTeachers();
        System.out.println("\nTEACHERS (" + teachers.size() + ")");
        if (teachers.isEmpty()) {
            System.out.println("No teachers in the database.");
            return;
        }
        for (Teacher teacher : teachers) {
            System.out.println(teacher + " | rate=" + teacher.getHourlyRateCents() + " cents/h");
        }
    }

    public void findTeacher(Scanner input) throws SQLException {
        System.out.print("Teacher ID: ");
        Teacher teacher = controller.getTeacherById(input.nextLine());

        System.out.println("\n-TEACHER DETAIL-");
        System.out.println("ID:             " + teacher.getId());
        System.out.println("Name:           " + teacher.getFullName());
        System.out.println("Hourly rate:    " + teacher.getHourlyRateCents() + " cents");
        System.out.println("Qualifications: " + teacher.getQualifications());
        System.out.println("Specializations:" + teacher.getSpecializations());
        System.out.println("Availability:");
        if (teacher.getAvailability().isEmpty()) {
            System.out.println("  (none)");
        } else {
            for (TimeSlot slot : teacher.getAvailability()) {
                System.out.println("  " + slot);
            }
        }
    }

    public void createTeacher(Scanner input) throws SQLException {
        System.out.println("\n-CREATE TEACHER-");
        controller.createTeacher(readTeacherFromInput(input));
        System.out.println("Teacher created.");
    }

    public void updateTeacher(Scanner input) throws SQLException {
        System.out.println("\n-UPDATE TEACHER-");
        controller.updateTeacher(readTeacherFromInput(input));
        System.out.println("Teacher updated.");
    }

    private Teacher readTeacherFromInput(Scanner input) {
        System.out.print("ID: ");
        String id = input.nextLine().trim();
        System.out.print("First name: ");
        String firstName = input.nextLine().trim();
        System.out.print("Last name: ");
        String lastName = input.nextLine().trim();
        System.out.print("Hourly rate (cents): ");
        long rate = Long.parseLong(input.nextLine().trim());
        System.out.print("Qualifications: ");
        String qualifications = input.nextLine().trim();
        return new Teacher(id, firstName, lastName, rate, qualifications);
    }

    // T04
    public void addSpecialization(Scanner input) throws SQLException {
        System.out.print("Teacher ID: ");
        String teacherId = input.nextLine().trim();
        System.out.print("Instrument type (PIANO/GUITAR/VIOLIN/FLUTE/DRUMS): ");
        String type = input.nextLine().trim();
        controller.addSpecialization(teacherId, type);
        System.out.println("Specialization added.");
    }

    public void addAvailability(Scanner input) throws SQLException {
        System.out.print("Teacher ID: ");
        String teacherId = input.nextLine().trim();
        LocalDateTime start = readDateTime(input, "Start");
        LocalDateTime end = readDateTime(input, "End");
        int slotNumber = controller.addAvailability(teacherId, start, end);
        System.out.println("Availability added (slot " + slotNumber + ").");
    }

    private LocalDateTime readDateTime(Scanner input, String label) {
        while (true) {
            System.out.print(label + " (yyyy-MM-dd HH:mm): ");
            String raw = input.nextLine().trim();
            try {
                return LocalDateTime.parse(raw, DT_FORMAT);
            } catch (DateTimeParseException error) {
                System.out.println("Invalid date-time. Example: 2026-11-01 09:00");
            }
        }
    }
}
