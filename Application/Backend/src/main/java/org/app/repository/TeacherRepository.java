package org.app.repository;
import org.app.database.DbTime;
import org.app.model.Teacher;
import org.app.model.TimeSlot;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeacherRepository {
    //T03
    public List<Teacher> findAll(Connection connection) throws SQLException {
        List<Teacher> teachers = new ArrayList<>();
        String sql = "SELECT * FROM teacher ORDER BY id";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet row = statement.executeQuery()) {
            while (row.next()) {
                teachers.add(readTeacher(connection, row));
            }
        }
        return teachers;
    }

    public Teacher findById(Connection connection, String id) throws SQLException {
        String sql = "SELECT * FROM teacher WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            try (ResultSet row = statement.executeQuery()) {
                if (row.next()) {
                    return readTeacher(connection, row);
                }
                return null;
            }
        }
    }

    public boolean existsById(Connection connection, String id) throws SQLException {
        String sql = "SELECT id FROM teacher WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            try (ResultSet row = statement.executeQuery()) {
                return row.next();
            }
        }
    }

    private Teacher readTeacher(Connection connection, ResultSet row) throws SQLException {
        Teacher teacher = new Teacher(
                row.getString("id"),
                row.getString("first_name"),
                row.getString("last_name"),
                row.getLong("hourly_rate_cents"),
                row.getString("qualifications")
        );
        for (String type : findSpecializations(connection, teacher.getId())) {
            teacher.addSpecialization(type);
        }
        for (TimeSlot slot : findAvailability(connection, teacher.getId())) {
            teacher.addAvailability(slot);
        }
        return teacher;
    }

    private List<String> findSpecializations(Connection connection, String teacherId) throws SQLException {
        List<String> types = new ArrayList<>();
        String sql = "SELECT instrument_type FROM teacher_specialization WHERE teacher_id = ? ORDER BY instrument_type";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, teacherId);
            try (ResultSet row = statement.executeQuery()) {
                while (row.next()) {
                    types.add(row.getString("instrument_type"));
                }
            }
        }
        return types;
    }

    private List<TimeSlot> findAvailability(Connection connection, String teacherId) throws SQLException {
        List<TimeSlot> slots = new ArrayList<>();
        String sql = "SELECT * FROM teacher_availability WHERE teacher_id = ? ORDER BY slot_number";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, teacherId);
            try (ResultSet row = statement.executeQuery()) {
                while (row.next()) {
                    slots.add(new TimeSlot(
                            DbTime.read(row, "starts_at"),
                            DbTime.read(row, "ends_at")
                    ));
                }
            }
        }
        return slots;
    }
    public void insert(Connection connection, Teacher teacher) throws SQLException {
        String sql = "INSERT INTO teacher "
                + "(id, first_name, last_name, hourly_rate_cents, qualifications) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, teacher.getId());
            statement.setString(2, teacher.getFirstName());
            statement.setString(3, teacher.getLastName());
            statement.setLong(4, teacher.getHourlyRateCents());
            statement.setString(5, teacher.getQualifications());
            if (statement.executeUpdate() != 1) {
                throw new SQLException("Teacher was not saved.");
            }
        }
    }

    public int updateBasic(Connection connection, Teacher teacher) throws SQLException {
        String sql = "UPDATE teacher SET "
                + "first_name = ?, last_name = ?, hourly_rate_cents = ?, qualifications = ? "
                + "WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, teacher.getFirstName());
            statement.setString(2, teacher.getLastName());
            statement.setLong(3, teacher.getHourlyRateCents());
            statement.setString(4, teacher.getQualifications());
            statement.setString(5, teacher.getId());    // WHERE: ID không đổi
            return statement.executeUpdate();
        }
    }

    // T04
    public void insertSpecialization(Connection connection, String teacherId, String type) throws SQLException {
        String sql = "INSERT INTO teacher_specialization (teacher_id, instrument_type) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, teacherId);
            statement.setString(2, type);
            statement.executeUpdate();
        }
    }

    public int nextSlotNumber(Connection connection, String teacherId) throws SQLException {
        String sql = "SELECT COALESCE(MAX(slot_number), 0) + 1 FROM teacher_availability WHERE teacher_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, teacherId);
            try (ResultSet row = statement.executeQuery()) {
                row.next();
                return row.getInt(1);
            }
        }
    }
    public void insertAvailability(Connection connection, String teacherId, int slotNumber, TimeSlot slot) throws SQLException {
        String sql = "INSERT INTO teacher_availability (teacher_id, slot_number, starts_at, ends_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, teacherId);
            statement.setInt(2, slotNumber);
            statement.setLong(3, DbTime.toEpochSeconds(slot.getStart()));
            statement.setLong(4, DbTime.toEpochSeconds(slot.getEnd()));
            statement.executeUpdate();
        }
    }
}

