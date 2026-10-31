package org.app.repository;
import org.app.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class StudentRepository {
    public List<Student> findAll(Connection connection) throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM student ORDER BY id";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet row = statement.executeQuery()) {
            while (row.next()) {
                students.add(readStudent(connection, row));
            }
        }
        return students;
    }

    private Student readStudent(Connection connection, ResultSet row) throws SQLException {
        String id = row.getString("id");
        return new Student(
                id,
                row.getString("first_name"),
                row.getString("last_name"),
                row.getString("address"),
                row.getDate("birth_date").toLocalDate(),
                row.getString("phone"),
                row.getString("email"),
                row.getString("level"),
                findPreferences(connection, id)
        );
    }
    public Student findById(Connection connection, String id) throws SQLException {
        String sql = "SELECT * FROM student WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            try (ResultSet row = statement.executeQuery()) {
                if (row.next()) {
                    return readStudent(connection, row);
                }
                return null;
            }
        }
    }
    private List<String> findPreferences(Connection connection, String studentId) throws SQLException {
        List<String> types = new ArrayList<>();
        String sql = "SELECT instrument_type FROM student_preference WHERE student_id = ? ORDER BY instrument_type";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            try (ResultSet row = statement.executeQuery()) {
                while (row.next()) {
                    types.add(row.getString("instrument_type"));
                }
            }
        }
        return types;
    }

    //T02
    public void insert(Connection connection, Student student) throws SQLException {
        String sql = "INSERT INTO student "
                + "(id, first_name, last_name, address, birth_date, phone, email, level) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, student.getId());
            statement.setString(2, student.getFirstName());
            statement.setString(3, student.getLastName());
            statement.setString(4, student.getAddress());
            statement.setDate(5, Date.valueOf(student.getBirthDate()));
            statement.setString(6, student.getPhone());
            statement.setString(7, student.getEmail());
            statement.setString(8, student.getLevel());
            if (statement.executeUpdate() != 1) {
                throw new SQLException("Student was not saved.");
            }
        }
    }
    public int updateBasic(Connection connection, Student student) throws SQLException {
        String sql = "UPDATE student SET "
                + "first_name = ?, last_name = ?, address = ?, birth_date = ?, "
                + "phone = ?, email = ?, level = ? "
                + "WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, student.getFirstName());
            statement.setString(2, student.getLastName());
            statement.setString(3, student.getAddress());
            statement.setDate(4, Date.valueOf(student.getBirthDate()));
            statement.setString(5, student.getPhone());
            statement.setString(6, student.getEmail());
            statement.setString(7, student.getLevel());
            statement.setString(8, student.getId());
            return statement.executeUpdate();
        }
    }
    public void insertPreferences(Connection connection, String studentId, List<String> types) throws SQLException {
        String sql = "INSERT INTO student_preference (student_id, instrument_type) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (String type : types) {
                statement.setString(1, studentId);
                statement.setString(2, type);
                statement.executeUpdate();
            }
        }
    }
    public void deletePreferences(Connection connection, String studentId) throws SQLException {
        String sql = "DELETE FROM student_preference WHERE student_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            statement.executeUpdate();
        }
    }
}