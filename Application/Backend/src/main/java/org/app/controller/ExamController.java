package org.app.controller;

import org.app.model.Exam;
import org.app.model.ExamRegistration;
import org.app.service.ExamService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class ExamController {

    private final ExamService service;

    public ExamController(ExamService service) {
        this.service = service;
    }

    public List<Exam> getAllExams() throws SQLException { return service.getAllExams(); }
    public Exam getExamById(String id) throws SQLException { return service.getExamById(id); }
    public void createExam(Exam exam) throws SQLException { service.createExam(exam); }
    public List<String> findConflicts(Exam exam) throws SQLException { return service.findConflicts(exam); }
    public void registerStudent(String examId, String studentId, LocalDateTime now) throws SQLException { service.registerStudent(examId, studentId, now); }
    public List<ExamRegistration> getRegistrations(String examId) throws SQLException { return service.getRegistrations(examId); }
    public void completeExam(String examId, LocalDateTime now) throws SQLException { service.completeExam(examId, now); }
    public void recordResult(String examId, String studentId, boolean passed, BigDecimal score, LocalDateTime now) throws SQLException {
        service.recordResult(examId, studentId, passed, score, now);
    }
}
