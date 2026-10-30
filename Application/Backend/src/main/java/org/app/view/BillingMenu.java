package org.app.view;

import org.app.controller.BillingController;
import org.app.model.Payment;
import org.app.model.Charge;

import java.util.List;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

    public void showStudentBilling(Scanner input) throws SQLException {
        String id = readId(input);

        List<Charge> charges = controller.getStudentCharges(id);
        List<Payment> payments = controller.getStudentPayments(id);
        long totalCharged = controller.getTotalChargedCents(id);
        long totalPaid = controller.getTotalPaidCents(id);
        long balance = controller.getBalanceCents(id);

        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        System.out.println("\n===== STUDENT BILLING: " + id + " =====");

        System.out.println("\nCHARGES:");
        if (charges.isEmpty()) System.out.println("No charges.");

        for (Charge charge : charges) {
            System.out.println(charge.getId() + " | " + charge.getAmountCents() + " cents | " + charge.getCreatedAt().format(format) + " | " + charge.getDescription());
        }

        System.out.println("\nPAYMENTS:");
        if (payments.isEmpty()) System.out.println("No payments.");

        for (Payment payment : payments) {
            System.out.println(payment.getId() + " | " + payment.getAmountCents() + " cents | " + payment.getPaidAt().format(format) + " | " + payment.getMethod());
        }

        System.out.println("\nTotal charged: " + totalCharged + " cents");
        System.out.println("Total paid: " + totalPaid + " cents");
        System.out.println("Outstanding balance: " + balance + " cents");
    }

    public void showMonthlyBilling(Scanner input) throws SQLException {
        YearMonth month = readMonth(input);
        long charged = controller.getMonthlyChargedCents(month);
        long paid = controller.getMonthlyPaidCents(month);

        System.out.println("\n===== SCHOOL BILLING: " + month + " =====");
        System.out.println("Charges created this month: " + charged + " cents");
        System.out.println("Payments received this month: " + paid + " cents");
    }

    private YearMonth readMonth(Scanner input) {
        while (true) {
            System.out.print("Month (yyyy-MM): ");

            try {
                return YearMonth.parse(input.nextLine().trim());
            } catch (DateTimeParseException error) {
                System.out.println("[ERROR] Enter a valid month, for example 2026-10.");
            }
        }
    }
}