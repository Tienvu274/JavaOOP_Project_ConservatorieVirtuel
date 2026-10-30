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

    //T01
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

    //T02
    public void createStudent(Student student) throws SQLException {
        if (student == null) {
            throw new IllegalArgumentException("Student is required.");
        }
        try (Connection connection = DbContext.open()) {
            connection.setAutoCommit(false);
            try {
                studentRepository.insert(connection, student);
                studentRepository.insertPreferences(connection,
                        student.getId(), student.getPreferredInstruments());
                connection.commit();
            } catch (SQLException | RuntimeException error) {
                connection.rollback();
                if (error instanceof SQLException sqlError && sqlError.getErrorCode() == 1062) {
                    throw new IllegalArgumentException("Student ID already exists: " + student.getId(), error);
                }
                throw error;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }
    public void updateStudent(Student student) throws SQLException {
        if (student == null) {
            throw new IllegalArgumentException("Student is required.");
        }

        try (Connection connection = DbContext.open()) {
            connection.setAutoCommit(false);
            try {
                int rows = studentRepository.updateBasic(connection, student);
                if (rows == 0) {
                    throw new IllegalArgumentException("Student not found: " + student.getId());
                }
                studentRepository.deletePreferences(connection, student.getId());
                studentRepository.insertPreferences(connection,
                        student.getId(), student.getPreferredInstruments());
                connection.commit();
            } catch (SQLException | RuntimeException error) {
                connection.rollback();
                throw error;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }
}

