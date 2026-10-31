package org.app.service;

import org.app.database.DbContext;
import org.app.model.Teacher;
import org.app.model.TimeSlot;
import org.app.repository.TeacherRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

public class TeacherService {

    private final TeacherRepository teacherRepository;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    public List<Teacher> getAllTeachers() throws SQLException {
        try (Connection connection = DbContext.open()) {
            return teacherRepository.findAll(connection);
        }
    }

    public Teacher getTeacherById(String id) throws SQLException {
        requireValidId(id);
        try (Connection connection = DbContext.open()) {
            Teacher teacher = teacherRepository.findById(connection, id.trim());
            if (teacher == null) {
                throw new IllegalArgumentException("Teacher not found: " + id.trim());
            }
            return teacher;
        }
    }

    public void createTeacher(Teacher teacher) throws SQLException {
        if (teacher == null) {
            throw new IllegalArgumentException("Teacher is required.");
        }
        try (Connection connection = DbContext.open()) {
            try {
                teacherRepository.insert(connection, teacher);
            } catch (SQLException error) {
                if (error.getErrorCode() == 1062) {
                    throw new IllegalArgumentException("Teacher ID already exists: " + teacher.getId(), error);
                }
                throw error;
            }
        }
    }

    public void updateTeacher(Teacher teacher) throws SQLException {
        if (teacher == null) {
            throw new IllegalArgumentException("Teacher is required.");
        }
        try (Connection connection = DbContext.open()) {
            int rows = teacherRepository.updateBasic(connection, teacher);
            if (rows == 0) {
                throw new IllegalArgumentException("Teacher not found: " + teacher.getId());
            }
        }
    }

    //T04
    public void addSpecialization(String teacherId, String instrumentType) throws SQLException {
        String type = normalizeInstrumentType(instrumentType);
        try (Connection connection = DbContext.open()) {
            if (!teacherRepository.existsById(connection, teacherId)) {
                throw new IllegalArgumentException("Teacher not found: " + teacherId);
            }
            try {
                teacherRepository.insertSpecialization(connection, teacherId, type);
            } catch (SQLException error) {
                if (error.getErrorCode() == 1062) {
                    throw new IllegalArgumentException("Teacher already has specialization: " + type, error);
                }
                throw error;
            }
        }
    }

    public int addAvailability(String teacherId, LocalDateTime start, LocalDateTime end) throws SQLException {
        TimeSlot slot = new TimeSlot(start, end);
        try (Connection connection = DbContext.open()) {
            if (!teacherRepository.existsById(connection, teacherId)) {
                throw new IllegalArgumentException("Teacher not found: " + teacherId);
            }
            int slotNumber = teacherRepository.nextSlotNumber(connection, teacherId);
            teacherRepository.insertAvailability(connection, teacherId, slotNumber, slot);
            return slotNumber;
        }
    }

    private void requireValidId(String id) {
        if (id == null || !id.trim().matches("[A-Za-z0-9_-]{1,40}")) {
            throw new IllegalArgumentException("Invalid teacher ID. Use 1-40 letters, digits, _ or -.");
        }
    }

    private String normalizeInstrumentType(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Instrument type is required.");
        }
        String t = type.trim().toUpperCase(Locale.ROOT);
        if (!t.equals("PIANO") && !t.equals("GUITAR") && !t.equals("VIOLIN")
                && !t.equals("FLUTE") && !t.equals("DRUMS")) {
            throw new IllegalArgumentException("Unknown instrument type: " + type);
        }
        return t;
    }
}
