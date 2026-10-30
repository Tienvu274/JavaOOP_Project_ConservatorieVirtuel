package org.app.view;

import org.app.controller.BillingController;
import org.app.model.Payment;

import java.sql.SQLException;
import java.util.Locale;
import java.util.Scanner;

public class BillingMenu {
    private final BillingController controller;

    public BillingMenu(BillingController controller) {
        this.controller = controller;
    }

    public void recordPayment(Scanner input) throws SQLException {
        String studentId = readId(input);
        long balance = controller.getBalanceCents(studentId);
        System.out.println("Outstanding balance: " + balance + " cents");
        if (balance <= 0) {
            System.out.println("No outstanding balance to pay.");
            return;
        }

        long amount = readAmount(input, balance);
        String method = readMethod(input);
        Payment payment = controller.recordPayment(studentId, amount, method);
        System.out.println("[OK] Payment saved: " + payment);
        System.out.println("Paid at: " + payment.getPaidAt());

        try {
            System.out.println("Remaining balance: " + controller.getBalanceCents(studentId) + " cents");
        } catch (SQLException error) {
            System.out.println("Payment saved, but balance refresh failed. Use option 25 to check again.");
        }
    }

    public void showBalance(Scanner input) throws SQLException {
        String studentId = readId(input);
        System.out.println("Outstanding balance: " + controller.getBalanceCents(studentId) + " cents");
    }

    private String readId(Scanner input) {
        while (true) {
            System.out.print("Student ID: ");
            String value = input.nextLine().trim();
            if (value.matches("[A-Za-z0-9_-]{1,40}")) return value;
            System.out.println("[ERROR] Use 1-40 letters, digits, underscores or hyphens.");
        }
    }

    private long readAmount(Scanner input, long balance) {
        while (true) {
            System.out.print("Amount in cents (20.50 EUR = 2050): ");
            try {
                long value = Long.parseLong(input.nextLine().trim());
                if (value > 0 && value <= balance) return value;
            } catch (NumberFormatException error) {
                System.out.println("[ERROR] Enter a whole number from 1 to " + balance + ".");
            }
        }
    }

    private String readMethod(Scanner input) {
        while (true) {
            System.out.print("Method (CASH/CARD/TRANSFER): ");
            String value = input.nextLine().trim().toUpperCase(Locale.ROOT);
            if (value.equals("CASH") || value.equals("CARD") || value.equals("TRANSFER")) return value;
            System.out.println("[ERROR] Unknown payment method.");
        }
    }
}