package org.app.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Exam extends Activity {

    private final String name;
    private final String instrumentType;
    private final String roomId;
    private final int capacity;

    private final List<String> instrumentIds = new ArrayList<>();

    private final List<ExamRegistration> registrations = new ArrayList<>();

    public Exam(String id, String name, String instrumentType, String roomId, LocalDateTime start, int durationMinutes, int capacity, List<String> instrumentIds, Status status, LocalDateTime changedAt, List<ExamRegistration> registrations) {
        super(id, start, durationMinutes, status, changedAt);
        this.name = ModelChecks.text(name, "Exam name");
        this.instrumentType = ModelChecks.type(instrumentType);
        this.roomId = ModelChecks.id(roomId);
        ModelChecks.require(capacity > 0, "Capacity must be positive!");
        this.capacity = capacity;

        for (String instrumentId : instrumentIds) {
            instrumentId = ModelChecks.id(instrumentId);
            ModelChecks.require(!this.instrumentIds.contains(instrumentId), "Duplicate instrument!");
            this.instrumentIds.add(instrumentId);
        }

        for (ExamRegistration registration : registrations) {
            ModelChecks.require(id.equals(registration.getExamId()), "Wrong exam ID!");

            for (ExamRegistration existing : this.registrations) {
                ModelChecks.require(!existing.getStudentId().equals(registration.getStudentId()), "Duplicate candidate!");
            }
            ExamResult result = registration.getResult();
            ModelChecks.require(result == null || (status == Status.COMPLETED && !result.getRecordedAt().isBefore(getEnd())), "Result requires a completed exam after its end!"
            );

            this.registrations.add(new ExamRegistration(registration)
            );
        }

        ModelChecks.require(this.registrations.size() <= capacity, "Exam exceeds capacity!");
    }

    public Exam(Exam other) {
        this(other.getId(), other.name, other.instrumentType, other.roomId, other.getStart(), other.getDurationMinutes(), other.capacity, other.instrumentIds, other.getStatus(), other.getChangedAt(), other.registrations);
    }

    public void register(String studentId, LocalDateTime now) {
        if (getStatus() != Status.SCHEDULED || !now.isBefore(getStart())) {
            throw new IllegalStateException("Exam registration closed!");
        }
        studentId = ModelChecks.id(studentId);
        for (ExamRegistration registration : registrations) {
            if (registration.getStudentId().equals(studentId)) {
                throw new IllegalStateException("Already registered!");
            }
        }
        if (registrations.size() >= capacity) {
            throw new IllegalStateException("Exam is full!");
        }
        registrations.add(new ExamRegistration(getId(), studentId, now, null));
    }

    public void recordResult(String studentId, boolean passed, BigDecimal score, LocalDateTime now) {
        if (getStatus() != Status.COMPLETED || now.isBefore(getEnd())) {
            throw new IllegalStateException("Complete exam before recording results!");
        }
        for (int i = 0; i < registrations.size(); i++) {
            ExamRegistration registration = registrations.get(i);
            if (registration.getStudentId().equals(studentId)) {
                registrations.set(i, registration.withResult(new ExamResult(passed, score, now)));
                return;
            }
        }
        throw new IllegalStateException("Student is not registered"
        );
    }

    public String getName() {
        return name;
    }

    public String getInstrumentType() {
        return instrumentType;
    }

    public String getRoomId() {
        return roomId;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<String> getInstrumentIds() {
        return new ArrayList<>(instrumentIds);
    }

    public List<ExamRegistration> getRegistrations() {
        List<ExamRegistration> result = new ArrayList<>();
        for (ExamRegistration registration : registrations) {
            result.add(new ExamRegistration(registration));
        }
        return result;
    }

    @Override
    public String getKind() {
        return "EXAM";
    }

    @Override
    public Activity copy() {
        return new Exam(this);
    }
}