package org.app.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Exam extends Activity {

    public record Result(boolean passed, Double score) {
    }

    private final String name;
    private final String instrumentType;
    private final String roomId;
    private final int capacity;

    private final Map<String, Result> candidates = new LinkedHashMap<>();

    public Exam(String id, String name, String instrumentType, String roomId, LocalDateTime start, int durationMinutes, int capacity) {
        super(id, start, durationMinutes);
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.name = name;
        this.instrumentType = instrumentType.toUpperCase();
        this.roomId = roomId;
        this.capacity = capacity;
    }

    public void register(String studentId) {
        if (candidates.containsKey(studentId)) {
            throw new IllegalStateException("Already registered");
        }
        if (candidates.size() >= capacity) {
            throw new IllegalStateException("Exam is full");
        }
        candidates.put(studentId, null);
    }

    public void recordResult(String studentId, boolean passed, Double score) {
        if (!candidates.containsKey(studentId)) {
            throw new IllegalStateException("Not registered");
        }
        if (score != null && score < 0) {
            throw new IllegalArgumentException("Score must be non-negative");
        }
        candidates.put(studentId, new Result(passed, score));
    }

    public Map<String, Result> getCandidates() {
        return Collections.unmodifiableMap(candidates);
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

    @Override
    public String getKind() {
        return "EXAM";
    }
}
