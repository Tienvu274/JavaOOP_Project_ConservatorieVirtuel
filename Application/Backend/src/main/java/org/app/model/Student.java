package org.app.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Student extends Person {
    private final String address;
    private final LocalDate birthDate;
    private final String phone;
    private final String email;
    private final String level;
    private final List<String> preferredInstruments = new ArrayList<>();

    public Student(String id, String firstName, String lastName, String address, LocalDate birthDate, String phone, String email, String level, List<String> preferredInstruments) {
        super(id, firstName, lastName);
        this.address = ModelChecks.text(address, "Address");
        this.birthDate = Objects.requireNonNull(birthDate, "Birth date is required!");
        ModelChecks.require(!birthDate.isAfter(LocalDate.now()), "Birth date is in the future!");
        this.phone = ModelChecks.text(phone, "Phone");
        this.email = ModelChecks.text(email, "Email");
        this.level = ModelChecks.text(level, "Level").toUpperCase(Locale.ROOT);
        ModelChecks.require(this.level.equals("BEGINNER") || this.level.equals("INTERMEDIATE") || this.level.equals("ADVANCED"), "Unknown student level");
        Objects.requireNonNull(preferredInstruments, "Preferences are required");
        for (String type : preferredInstruments) {
            String normalized = ModelChecks.type(type);
            ModelChecks.require(!this.preferredInstruments.contains(normalized), "Duplicate preference!");
            this.preferredInstruments.add(normalized);
        }
    }

    public Student(Student stu) {
       this(
               stu.getId(),
               stu.getFirstName(),
               stu.getLastName(),
               stu.address,
               stu.birthDate,
               stu.phone,
               stu.email,
               stu.level,
               stu.preferredInstruments
       );
    }

    public String getAddress() {
        return address;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getLevel() {
        return level;
    }

    public List<String> getPreferredInstruments() {
        return preferredInstruments;
    }

    @Override
    public String getRole() {
        return "STUDENT";
    }

    @Override
    public String toString() {
        return getId() + " - " + getFullName() + " (" + level + ")";
    }
}
