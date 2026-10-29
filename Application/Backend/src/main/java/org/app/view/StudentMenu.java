package org.app.view;

import org.app.controller.StudentController;
import org.app.model.Student;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class StudentMenu {

    private final StudentController controller;

    public StudentMenu(StudentController controller) {
        this.controller = controller;
    }

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

        System.out.println("\n---STUDENT DETAIL---");
        System.out.println("ID:          " + student.getId());
        System.out.println("Name:        " + student.getFullName());
        System.out.println("DOB:         " + student.getBirthDate());
        System.out.println("Address:     " + student.getAddress());
        System.out.println("Phone:       " + student.getPhone());
        System.out.println("Email:       " + student.getEmail());
        System.out.println("Level:       " + student.getLevel());
        System.out.println("Instruments: " + student.getPreferredInstruments());
    }
}
