package org.app.controller;

import org.app.model.Student;
import org.app.service.StudentService;

import java.sql.SQLException;
import java.util.List;

public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    public List<Student> getAllStudents() throws SQLException {
        return service.getAllStudents();
    }

    public Student getStudentById(String id) throws SQLException {
        return service.getStudentById(id);
    }

    public void createStudent(Student student) throws SQLException {
        service.createStudent(student);
    }
    public void updateStudent(Student student) throws SQLException {
        service.updateStudent(student);
    }
}
