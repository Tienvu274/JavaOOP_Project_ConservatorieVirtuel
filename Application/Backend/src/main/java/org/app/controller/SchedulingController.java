package org.app.controller;

import org.app.database.DbTime;
import org.app.model.Lesson;
import org.app.service.SchedulingService;
import org.app.model.Activity;

import java.util.List;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class SchedulingController {
    private final SchedulingService service;

    public SchedulingController(SchedulingService service) {
        this.service = service;
    }

    public Lesson scheduleLesson(Lesson lesson) throws SQLException {
        return service.scheduleLesson(lesson, LocalDateTime.now(DbTime.ZONE).withNano(0));
    }

    public Lesson cancelLesson(String lessonId) throws SQLException {
        return service.cancelLesson(lessonId, LocalDateTime.now(DbTime.ZONE).withNano(0));
    }

    public Lesson completeLesson(String lessonId) throws SQLException {
        return service.completeLesson(lessonId, LocalDateTime.now(DbTime.ZONE).withNano(0));
    }

    public List<Activity> getTeacherActivities(String teacherId) throws SQLException {
        return service.getTeacherActivities(teacherId);
    }

    public List<Activity> getStudentActivities(String studentId) throws SQLException {
        return service.getStudentActivities(studentId);
    }
}