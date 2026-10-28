package org.app.view;

import org.app.controller.PackageController;
import org.app.model.CoursePackage;
import org.app.model.FinitePackage;
import org.app.model.UnlimitedPackage;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class PackageMenu {

    private final PackageController controller;

    public PackageMenu(PackageController controller) {
        this.controller = controller;
    }

    public void createPackage(Scanner input) throws SQLException {

        System.out.print("Package ID: ");
        String id = input.nextLine().trim();

        System.out.print("Package name: ");
        String name = input.nextLine().trim();

        long priceCents = readWholeNumber(input, "Price in cents (20.50 EUR = 2050 cents): ", 0, Long.MAX_VALUE);

        int type = (int) readWholeNumber(input, "Type (1 = finite, 2 = unlimited): ", 1, 2);

        CoursePackage coursePackage;

        if (type == 1) {
            int lessonCount = (int) readWholeNumber(input, "Number of lessons: ", 1, Integer.MAX_VALUE / 60);
            coursePackage = new FinitePackage(id, name, priceCents, lessonCount);
        } else {
            coursePackage = new UnlimitedPackage(id, name, priceCents);
        }
        CoursePackage savedPackage = controller.createPackage(coursePackage);

        System.out.println("[OK] Package created:");
        System.out.println(savedPackage);
    }

    public void listPackages() throws SQLException {
        List<CoursePackage> packages = controller.getAllPackages();
        if (packages.isEmpty()) {
            System.out.println("No packages found.");
            return;
        }
        System.out.println("\n===== COURSE PACKAGES =====");
        for (CoursePackage coursePackage : packages) {
            System.out.println(coursePackage);
        }
    }

    private long readWholeNumber(Scanner input, String prompt, long min, long max) {
        while (true) {
            System.out.print(prompt);
            String value = input.nextLine().trim();
            try {
                long number = Long.parseLong(value);
                if (number < min || number > max) {
                    System.out.println("[ERROR] Enter a value between " + min + " and " + max + ".");
                    continue;
                }
                return number;
            } catch (NumberFormatException error) {
                System.out.println("[ERROR] Enter a whole number without decimals or separators.");
            }
        }
    }
}