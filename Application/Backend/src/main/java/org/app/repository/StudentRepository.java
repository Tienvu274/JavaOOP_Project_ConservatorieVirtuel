package org.app.repository;
import org.app.model.Student;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class StudentRepository {
    public List<Student> findAll(Connection connection) throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM student ORDER BY id";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet row = statement.executeQuery()) {
            while (row.next()) {
                String id = row.getString("id");
                Student student = new Student(
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
                students.add(student);
            }
        }
        return students;
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
}