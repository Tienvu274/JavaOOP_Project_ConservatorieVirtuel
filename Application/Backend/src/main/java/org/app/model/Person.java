package org.app.model;

public abstract class Person {
    private String id;
    private String firstName;
    private String lastName;

    protected Person(String id, String firstName, String lastName) {
        this.id = ModelChecks.id(id);
        this.firstName = ModelChecks.text(firstName, "First name");
        this.lastName = ModelChecks.text(lastName, "Last name");
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public abstract String getRole();

    @Override
    public String toString() {
        return id + " - " + getFullName() + " [" + getRole() + "]";
    }
}
