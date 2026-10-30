package org.app.view;

import org.app.controller.RentalController;
import org.app.database.DbTime;
import org.app.model.Rental;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Scanner;

public class RentalMenu {

    private final RentalController controller;

    public RentalMenu(RentalController controller) {
        this.controller = controller;
    }

    public void bookRoom(Scanner input) throws SQLException {
        rent(input, Rental.ResourceType.ROOM);
    }

    public void rentInstrument(Scanner input) throws SQLException {
        rent(input, Rental.ResourceType.INSTRUMENT);
    }

    private void rent(Scanner input, Rental.ResourceType type) throws SQLException {
        String id = readId(input, "Rental ID: ");
        String studentId = readId(input, "Student ID: ");
        String resourceId = readId(input, type == Rental.ResourceType.ROOM ? "Room ID: " : "Instrument ID: ");
        LocalDateTime start = readStart(input);
        int minutes = readMinutes(input);

        Rental rental = controller.createRental(id, studentId, type, resourceId, start, minutes);

        System.out.println("[OK] Rental and charge saved: " + rental.getId());
        System.out.println("Resource: " + rental.getResourceType() + " | " + rental.getResourceId());
        System.out.println("Time: " + rental.getStart() + " -> " + rental.getEnd());
        System.out.println("Amount due: " + rental.getPriceCents() + " cents");
        System.out.println("No payment has been recorded.");
    }

    private String readId(Scanner input, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = input.nextLine().trim();
            if (value.matches("[A-Za-z0-9_-]{1,40}")) return value;
            System.out.println("[ERROR] Use 1-40 letters, digits, underscores or hyphens.");
        }
    }

    private int readMinutes(Scanner input) {
        while (true) {
            System.out.print("Duration in minutes: ");
            try {
                int value = Integer.parseInt(input.nextLine().trim());
                if (value > 0) return value;
            } catch (NumberFormatException error) {
                System.out.println("[ERROR] Enter a positive whole number.");
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
}