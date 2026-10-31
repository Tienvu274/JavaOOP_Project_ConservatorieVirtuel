package org.app.view;

import org.app.controller.ExamController;
import org.app.database.DbTime;
import org.app.model.Activity;
import org.app.model.Exam;
import org.app.model.ExamRegistration;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ExamMenu {

    private final ExamController controller;
    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ExamMenu(ExamController controller) {
        this.controller = controller;
    }

    //T07
    public void listExams() throws SQLException {
        List<Exam> exams = controller.getAllExams();
        System.out.println("\nEXAMS (" + exams.size() + ")");
        if (exams.isEmpty()) {
            System.out.println("No exams.");
            return;
        }
        for (Exam exam : exams) {
            System.out.println(exam.getId() + " | " + exam.getName()
                    + " | " + exam.getInstrumentType()
                    + " | " + exam.getStart() + " (" + exam.getDurationMinutes() + " min)"
                    + " | room " + exam.getRoomId()
                    + " | cap " + exam.getCapacity()
                    + " | " + exam.getStatus()
                    + " | registered " + exam.getRegistrations().size());
        }
    }

    public void findExam(Scanner input) throws SQLException {
        System.out.print("Exam ID: ");
        Exam exam = controller.getExamById(input.nextLine().trim());
        System.out.println("\n-EXAM DETAIL-");
        System.out.println("ID:         " + exam.getId());
        System.out.println("Name:       " + exam.getName());
        System.out.println("Instrument: " + exam.getInstrumentType());
        System.out.println("Room:       " + exam.getRoomId());
        System.out.println("Start:      " + exam.getStart());
        System.out.println("Duration:   " + exam.getDurationMinutes() + " min");
        System.out.println("Capacity:   " + exam.getCapacity());
        System.out.println("Status:     " + exam.getStatus());
        System.out.println("Instruments:" + exam.getInstrumentIds());
        System.out.println("Registrations:");
        for (ExamRegistration r : exam.getRegistrations()) {
            System.out.println("  " + r);
        }
    }

    public void createExam(Scanner input) throws SQLException {
        System.out.println("\n-CREATE EXAM-");
        controller.createExam(readExamFromInput(input));
        System.out.println("Exam created.");
    }

    //T08
    public void checkConflicts(Scanner input) throws SQLException {
        System.out.println("\n-CHECK EXAM CONFLICTS-");
        Exam exam = readExamFromInput(input);
        List<String> conflicts = controller.findConflicts(exam);
        if (conflicts.isEmpty()) {
            System.out.println("No conflicts. This exam can be scheduled.");
        } else {
            System.out.println("Conflicts found:");
            for (String c : conflicts) {
                System.out.println("  " + c);
            }
        }
    }

    //T09
    public void registerStudent(Scanner input) throws SQLException {
        System.out.print("Exam ID: ");
        String examId = input.nextLine().trim();
        System.out.print("Student ID: ");
        String studentId = input.nextLine().trim();
        controller.registerStudent(examId, studentId, now());
        System.out.println("Student registered.");
    }

    public void showRegistrations(Scanner input) throws SQLException {
        System.out.print("Exam ID: ");
        List<ExamRegistration> list = controller.getRegistrations(input.nextLine().trim());
        System.out.println("\nREGISTRATIONS (" + list.size() + ")");
        for (ExamRegistration r : list) {
            System.out.println("  " + r);
        }
    }

    //T10
    public void completeExam(Scanner input) throws SQLException {
        System.out.print("Exam ID: ");
        controller.completeExam(input.nextLine().trim(), now());
        System.out.println("Exam completed.");
    }

    public void recordResult(Scanner input) throws SQLException {
        System.out.print("Exam ID: ");
        String examId = input.nextLine().trim();
        System.out.print("Student ID: ");
        String studentId = input.nextLine().trim();
        System.out.print("Passed? (y/n): ");
        boolean passed = input.nextLine().trim().equalsIgnoreCase("y");
        System.out.print("Score: ");
        String rawScore = input.nextLine().trim();
        BigDecimal score = rawScore.isEmpty() ? null : new BigDecimal(rawScore);
        controller.recordResult(examId, studentId, passed, score, now());
        System.out.println("Result recorded.");
    }

    //helper
    private Exam readExamFromInput(Scanner input) {
        System.out.print("ID: ");
        String id = input.nextLine().trim();
        System.out.print("Name: ");
        String name = input.nextLine().trim();
        System.out.print("Instrument type (PIANO/GUITAR/VIOLIN/FLUTE/DRUMS): ");
        String type = input.nextLine().trim();
        System.out.print("Room ID: ");
        String roomId = input.nextLine().trim();
        LocalDateTime start = readDateTime(input, "Start");
        System.out.print("Duration (minutes): ");
        int duration = Integer.parseInt(input.nextLine().trim());
        System.out.print("Capacity: ");
        int capacity = Integer.parseInt(input.nextLine().trim());
        System.out.print("Instrument IDs, comma-separated, or blank: ");
        String raw = input.nextLine().trim();
        List<String> instrumentIds = new ArrayList<>();
        if (!raw.isEmpty()) {
            for (String part : raw.split(",")) {
                if (!part.trim().isEmpty()) instrumentIds.add(part.trim());
            }
        }
        return new Exam(id, name, type, roomId, start, duration, capacity,
                instrumentIds, Activity.Status.SCHEDULED, null, new ArrayList<>());
    }

    private LocalDateTime now() {
        return LocalDateTime.now(DbTime.ZONE).withNano(0);
    }

    private LocalDateTime readDateTime(Scanner input, String label) {
        while (true) {
            System.out.print(label + " (yyyy-MM-dd HH:mm): ");
            String raw = input.nextLine().trim();
            try {
                return LocalDateTime.parse(raw, DT_FORMAT);
            } catch (DateTimeParseException error) {
                System.out.println("Invalid date-time. (yyyy-MM-dd HH:mm)");
            }
        }
    }
}
