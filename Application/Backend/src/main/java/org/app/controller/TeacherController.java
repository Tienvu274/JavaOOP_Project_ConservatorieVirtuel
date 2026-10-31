package org.app.controller;

import org.app.model.Teacher;
import org.app.service.TeacherService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class TeacherController {

    private final TeacherService service;

    public TeacherController(TeacherService service) {
        this.service = service;
    }

    public List<Teacher> getAllTeachers() throws SQLException {
        return service.getAllTeachers();
    }

    public Teacher getTeacherById(String id) throws SQLException {
        return service.getTeacherById(id);
    }

    public void createTeacher(Teacher teacher) throws SQLException {
        service.createTeacher(teacher);
    }

    public void updateTeacher(Teacher teacher) throws SQLException {
        service.updateTeacher(teacher);
    }

    public void addSpecialization(String teacherId, String instrumentType) throws SQLException {
        service.addSpecialization(teacherId, instrumentType);
    }

    public int addAvailability(String teacherId, LocalDateTime start, LocalDateTime end) throws SQLException {
        return service.addAvailability(teacherId, start, end);
    }
}
