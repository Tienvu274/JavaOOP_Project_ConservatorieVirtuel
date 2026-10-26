package org.app.model;

public class Student extends Person {
    private final String email;
    private final String level;

    public Student(String id, String firstName, String lastName, String email, String level) {
        super(id, firstName, lastName);
        this.email = email;
        this.level = level;
    }

    public Student(Student other) {
        this(other.getId(), other.getFirstName(), other.getLastName(), other.email, other.level);
    }

    public String getEmail() {
        return email;
    }

    public String getLevel() {
        return level;
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
