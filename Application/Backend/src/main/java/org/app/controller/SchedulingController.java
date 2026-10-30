package org.app.controller;

import org.app.database.DbTime;
import org.app.model.Lesson;
import org.app.service.SchedulingService;

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
}