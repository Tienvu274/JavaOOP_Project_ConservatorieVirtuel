package org.app.view;

import org.app.controller.StudentController;
import org.app.model.Student;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StudentMenu {

    private final StudentController controller;
//    private static final DateTimeFormatter DOB_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public StudentMenu(StudentController controller) {
        this.controller = controller;
    }
    //T01: Read + Find
    public void listStudents() throws SQLException {
        List<Student> students = controller.getAllStudents();
        System.out.println("\nSTUDENTS (" + students.size() + ")");
        if (students.isEmpty()) {
            System.out.println("No students in the database.");
            return;
        }
        for (Student student : students) {
            System.out.println(student + " | instruments: " + student.getPreferredInstruments());
        }
    }

    public void findStudent(Scanner input) throws SQLException {
        System.out.print("Student ID: ");
        String id = input.nextLine();
        Student student = controller.getStudentById(id);

        System.out.println("\n-STUDENT DETAIL-");
        System.out.println("ID:          " + student.getId());
        System.out.println("Name:        " + student.getFullName());
        System.out.println("DOB:         " + student.getBirthDate());
        System.out.println("Address:     " + student.getAddress());
        System.out.println("Phone:       " + student.getPhone());
        System.out.println("Email:       " + student.getEmail());
        System.out.println("Level:       " + student.getLevel());
        System.out.println("Instruments: " + student.getPreferredInstruments());
    }

    //T02: Update + Add
    public void createStudent(Scanner input) throws SQLException {
        System.out.println("\n-CREATE STUDENT-");
        Student student = readStudentFromInput(input);
        controller.createStudent(student);
        System.out.println("Student created: " + student);
    }
    public void updateStudent(Scanner input) throws SQLException {
        System.out.println("\n-UPDATE STUDENT-");
        Student student = readStudentFromInput(input);
        controller.updateStudent(student);
        System.out.println("Student updated: " + student);
    }
    private Student readStudentFromInput(Scanner input) {
        System.out.print("ID: ");
        String id = input.nextLine().trim();
        System.out.print("First name: ");
        String firstName = input.nextLine().trim();
        System.out.print("Last name: ");
        String lastName = input.nextLine().trim();
        System.out.print("Address: ");
        String address = input.nextLine().trim();
//        System.out.print("DOB (yyyy-MM-dd): ");
//        LocalDate birthDate = LocalDate.parse(input.nextLine().trim());
        LocalDate birthDate = readBirthDate(input);
        System.out.print("Phone: ");
        String phone = input.nextLine().trim();
        System.out.print("Email: ");
        String email = input.nextLine().trim();
        System.out.print("Level (BEGINNER/INTERMEDIATE/ADVANCED): ");
        String level = input.nextLine().trim();
        System.out.print("Preferred instruments (PIANO,GUITAR,...): ");
        String raw = input.nextLine().trim();
        List<String> preferences = new ArrayList<>();
        if (!raw.isEmpty()) {
            for (String part : raw.split(",")) {
                if (!part.trim().isEmpty()) {
                    preferences.add(part.trim());
                }
            }
        }

        return new Student(id, firstName, lastName, address, birthDate, phone, email, level, preferences);
    }
    private LocalDate readBirthDate(Scanner input) {
        while (true) {
            System.out.print("DOB (yyyy-MM-dd): ");
            String raw = input.nextLine().trim();
            try {
                return LocalDate.parse(raw);
            } catch (DateTimeParseException error) {
                System.out.println("Invalid date format.");
            }
        }
    }
}
