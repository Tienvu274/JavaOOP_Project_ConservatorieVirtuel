package org.app.model;

import java.time.LocalDateTime;
import java.util.*;

public class Teacher extends Person{
    private final long hourlyRateCents;
    private final String qualifications;

    private final List<String> specializations = new ArrayList<>();
    private final List<TimeSlot> availability = new ArrayList<>();

    public Teacher(String id, String firstName, String lastName, long hourlyRateCents, String qualifications) {
        super(id, firstName, lastName);
        this.hourlyRateCents = ModelChecks.price(hourlyRateCents);
        this.qualifications = ModelChecks.text(qualifications, "Qualifications");
    }

    public Teacher(Teacher other) {
        this(other.getId(), other.getFirstName(), other.getLastName(), other.hourlyRateCents, other.qualifications);
        specializations.addAll(other.specializations);
        for (TimeSlot slot : other.availability) {
            availability.add(new TimeSlot(slot));
        }
    }

    public void addSpecialization(String instrumentType) {
        instrumentType = ModelChecks.type(instrumentType);
        ModelChecks.require(!specializations.contains(instrumentType), "Duplicate specialization!");
        specializations.add(instrumentType);
    }

    public void addAvailability(TimeSlot slot) {
        availability.add(new TimeSlot(slot));
    }

    public boolean canTeach(String instrumentType) {
        return specializations.contains(ModelChecks.type(instrumentType));
    }

    public boolean canTeach(String instrumentType, LocalDateTime start, LocalDateTime end) {
        if (!canTeach(instrumentType)) {
            return false;
        }
        for (TimeSlot slot : availability) {
            if (slot.covers(start, end)) {
                return true;
            }
        }
        return false;
    }

    public List<String> getSpecializations() {
        return new ArrayList<>(specializations);
    }

    public List<TimeSlot> getAvailability() {
        List<TimeSlot> result = new ArrayList<>();
        for (TimeSlot slot : availability) {
            result.add(new TimeSlot(slot));
        }
        return result;
    }

    public long getHourlyRateCents() {
        return hourlyRateCents;
    }

    public String getQualifications() {
        return qualifications;
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
