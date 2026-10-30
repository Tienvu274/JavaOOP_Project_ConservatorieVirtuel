package org.app.view;

import org.app.controller.SchedulingController;
import org.app.database.DbTime;
import org.app.model.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class SchedulingMenu {
    private final SchedulingController controller;

    public SchedulingMenu(SchedulingController controller) {
        this.controller = controller;
    }

    public void scheduleLesson(Scanner input) throws SQLException {
        String id = readId(input, "Lesson ID: ", false);
        LocalDateTime start = readStart(input);
        String teacherId = readId(input, "Teacher ID: ", false);
        String roomId = readId(input, "Room ID: ", false);
        String instrumentType = readType(input);
        List<String> instruments = new ArrayList<>();
        instruments.add(readId(input, "Instrument ID: ", false));
        boolean group = readInt(input, "Type (1 = individual, 2 = group): ", 1, 2) == 2;

        int count = group ? readInt(input, "Number of students: ", 2, 100) : 1;

        List<LessonParticipation> participants = new ArrayList<>();
        List<String> selectedStudents = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            String studentId;
            while (true) {
                studentId = readId(input, "Student " + (i + 1) + " ID: ", false);
                if (!selectedStudents.contains(studentId)) {
                    break;
                }
                System.out.println("[ERROR] Student already selected.");
            }
            selectedStudents.add(studentId);
            String enrollmentId = readId(input, "Enrollment ID (Enter = single paid lesson): ", true);
            participants.add(new LessonParticipation(
                            id,
                            studentId,
                            enrollmentId,
                            60,
                            LessonParticipation.State.RESERVED,
                            LocalDateTime.now(DbTime.ZONE).withNano(0),
                            null
                    )
            );
        }

        Lesson draft = new Lesson(id, start, teacherId, roomId, instrumentType, group, instruments, Activity.Status.SCHEDULED, null, participants);
        Lesson saved = controller.scheduleLesson(draft);
        System.out.println("[OK] Lesson saved: " + saved);
        for (LessonParticipation participant : saved.getParticipants()) {
            System.out.println(participant);
            if (participant.getEnrollmentId() == null) {
                System.out.println("Single lesson charge created. Check billing details in option 8."
                );
            }
        }
    }

    private String readId(Scanner input, String prompt, boolean optional) {
        while (true) {
            System.out.print(prompt);
            String value = input.nextLine().trim();
            if (optional && value.isEmpty()) {
                return null;
            }
            if (value.matches("[A-Za-z0-9_-]{1,40}")) {
                return value;
            }
            System.out.println("[ERROR] Use 1-40 letters, digits, underscores or hyphens."
            );
        }
    }

    private int readInt(Scanner input, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(input.nextLine().trim());

                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException error) {
                System.out.println("[ERROR] Enter a whole number from " + min + " to " + max + ".");
            }
        }
    }

    private LocalDateTime readStart(Scanner input) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm").withResolverStyle(ResolverStyle.STRICT);

        while (true) {
            System.out.print("Start (yyyy-MM-dd HH:mm, Paris time): ");
            try {
                LocalDateTime value = LocalDateTime.parse(input.nextLine().trim(), format);
                if (!value.isAfter(LocalDateTime.now(DbTime.ZONE))) {
                    System.out.println("[ERROR] Choose a future time.");
                    continue;
                }
                DbTime.toEpochSeconds(value);
                return value;
            } catch (DateTimeParseException error) {
                System.out.println("[ERROR] Enter a valid date/time.");
            } catch (IllegalArgumentException error) {
                System.out.println("[ERROR] " + error.getMessage());
            }
        }
    }

    private String readType(Scanner input) {
        while (true) {
            System.out.print("Instrument type (PIANO/GUITAR/VIOLIN/FLUTE/DRUMS): ");
            String value = input.nextLine().trim().toUpperCase(Locale.ROOT);
            if (value.equals("PIANO")
                    || value.equals("GUITAR")
                    || value.equals("VIOLIN")
                    || value.equals("FLUTE")
                    || value.equals("DRUMS")) {
                return value;
            }
            System.out.println("[ERROR] Unknown instrument type.");
        }
    }
}