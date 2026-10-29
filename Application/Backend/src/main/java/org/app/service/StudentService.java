package org.app.service;
import org.app.database.DbContext;
import org.app.model.Student;
import org.app.repository.StudentRepository;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getAllStudents() throws SQLException {
        try (Connection connection = DbContext.open()) {
            return studentRepository.findAll(connection);
        }
    }

    public Student getStudentById(String id) throws SQLException {
        if (id == null || !id.trim().matches("[A-Za-z0-9_-]{1,40}")) {
            throw new IllegalArgumentException("Invalid student ID. Use 1-40 letters, digits, _ or -.");
        }
        id = id.trim();

        try (Connection connection = DbContext.open()) {
            Student student = studentRepository.findById(connection, id);
            if (student == null) {
                throw new IllegalArgumentException("Student not found: " + id);
            }
            return student;
        }
    }
}

