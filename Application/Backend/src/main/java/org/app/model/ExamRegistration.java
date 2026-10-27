package org.app.model;

import java.time.LocalDateTime;

public class ExamRegistration {

    private final String examId;
    private final String studentId;
    private final LocalDateTime registeredAt;
    private final ExamResult result;

    public ExamRegistration(String examId, String studentId, LocalDateTime registeredAt, ExamResult result) {
        this.examId = ModelChecks.id(examId);
        this.studentId = ModelChecks.id(studentId);
        this.registeredAt = ModelChecks.time(registeredAt);
        this.result = result == null ? null : new ExamResult(result);
    }

    public ExamRegistration(ExamRegistration other) {
        this(other.examId, other.studentId, other.registeredAt, other.result);
    }

    public ExamRegistration withResult(ExamResult value) {
        if (result != null) {
            throw new IllegalStateException("Result already recorded!");
        }
        ModelChecks.require(value != null && !value.getRecordedAt().isBefore(registeredAt), "Invalid result time!");
        return new ExamRegistration(examId, studentId, registeredAt, value);
    }

    public String getExamId() {
        return examId;
    }

    public String getStudentId() {
        return studentId;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public ExamResult getResult() {
        return result == null ? null : new ExamResult(result);
    }

    @Override
    public String toString() {
        return examId + "/" + studentId
                + " | "
                + (result == null ? "no result" : result);
    }
}