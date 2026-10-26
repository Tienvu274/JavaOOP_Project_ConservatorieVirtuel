package org.app.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class Teacher extends Person{
    private final long hourlyRateCents;
    private final String qualifications;

    private final Set<String> specializations = new LinkedHashSet<>();

    public Teacher(
            String id,
            String firstName,
            String lastName,
            long hourlyRateCents,
            String qualifications) {

        super(id, firstName, lastName);

        this.hourlyRateCents = hourlyRateCents;
        this.qualifications = qualifications;
    }

    public Teacher(Teacher other) {
        this(other.getId(), other.getFirstName(), other.getLastName(), other.hourlyRateCents, other.qualifications);
        this.specializations.addAll(other.specializations);
    }

    public void addSpecialization(String instrumentType) {
        specializations.add(instrumentType.toUpperCase());
    }

    public boolean canTeach(String instrumentType) {
        return specializations.contains(instrumentType.toUpperCase());
    }

    public Set<String> getSpecializations() {
        return Collections.unmodifiableSet(specializations);
    }

    public long getHourlyRateCents() {
        return hourlyRateCents;
    }

    @Override
    public String getRole() {
        return "TEACHER";
    }

    @Override
    public String toString() {
        return getId() + " - " + getFullName() + " " + specializations;
    }
}
