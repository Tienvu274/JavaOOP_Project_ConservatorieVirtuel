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

    public void cancelLesson(Scanner input) throws SQLException {
        String id = readId(input, "Lesson ID to cancel: ", false);
        Lesson lesson = controller.cancelLesson(id);

        System.out.println("[OK] Lesson cancelled: " + lesson.getId());

        for (LessonParticipation p : lesson.getParticipants()) {
            System.out.println(p.getStudentId() + " | " + p.getState());

            if (p.getEnrollmentId() == null) {
                System.out.println("Single paid lesson: billing records are unchanged.");
            } else if (p.getState() == LessonParticipation.State.RELEASED) {
                System.out.println("60 minutes released to enrollment " + p.getEnrollmentId());
            } else {
                System.out.println("Late cancellation: 60 minutes remain consumed.");
            }
        }
    }

    public void completeLesson(Scanner input) throws SQLException {
        String id = readId(input, "Lesson ID to complete: ", false);
        Lesson lesson = controller.completeLesson(id);
        System.out.println("[OK] Lesson completed: " + lesson.getId());
        for (LessonParticipation p : lesson.getParticipants()) {
            System.out.println(p.getStudentId() + " | " + p.getState() + " | " + p.getMinutes() + " minutes");
        }
        System.out.println("Reserved minutes are now consumed. No additional charge was created.");
    }

    public void showTeacherSchedule(Scanner input) throws SQLException {
        String id = readId(input, "Teacher ID: ", false);
        List<Activity> activities = controller.getTeacherActivities(id);

        System.out.println("\n===== TEACHER SCHEDULE: " + id + " =====");
        showActivities(activities);
    }

    public void showStudentSchedule(Scanner input) throws SQLException {
        String id = readId(input, "Student ID: ", false);
        List<Activity> activities = controller.getStudentActivities(id);

        System.out.println("\n===== STUDENT SCHEDULE: " + id + " =====");
        showActivities(activities);
    }

    private void showActivities(List<Activity> activities) {
        if (activities.isEmpty()) {
            System.out.println("No activities found.");
            return;
        }

        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Activity activity : activities) {
            System.out.println("\n" + activity.getId() + " | " + activity.getKind() + " | " + activity.getStatus());
            System.out.println("Time: " + activity.getStart().format(format) + " -> " + activity.getEnd().format(format));

            if (activity instanceof Lesson) {
                Lesson lesson = (Lesson) activity;

                System.out.println("Teacher: " + lesson.getTeacherId() + " | Room: " + lesson.getRoomId());
                System.out.println("Instrument type: " + lesson.getInstrumentType() + " | Instruments: " + lesson.getInstrumentIds());
                System.out.println("Lesson type: " + (lesson.isGroupLesson() ? "GROUP" : "INDIVIDUAL"));

                for (LessonParticipation p : lesson.getParticipants()) {
                    System.out.println("Student: " + p.getStudentId() + " | " + p.getState());
                }
            } else if (activity instanceof Exam) {
                Exam exam = (Exam) activity;

                System.out.println("Exam: " + exam.getName() + " | Instrument type: " + exam.getInstrumentType());
                System.out.println("Room: " + exam.getRoomId() + " | Instruments: " + exam.getInstrumentIds());

            } else if (activity instanceof Rental) {
                Rental rental = (Rental) activity;

                System.out.println("Student: " + rental.getStudentId());
                System.out.println("Resource: " + rental.getResourceType() + " | ID: " + rental.getResourceId());
            }
        }
    }
}