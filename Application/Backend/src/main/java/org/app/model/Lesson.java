package org.app.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Lesson extends Activity {

    private final String teacherId;
    private final String roomId;
    private final String instrumentId;

    private final Map<String, String> participants = new LinkedHashMap<>();

    public Lesson(String id, LocalDateTime start, String teacherId, String roomId, String instrumentId) {
        super(id, start, 60);
        this.teacherId = teacherId;
        this.roomId = roomId;
        this.instrumentId = instrumentId;
    }

    public void addParticipant(String studentId, String enrollmentId) {
        if (participants.containsKey(studentId)) {
            throw new IllegalArgumentException("Duplicate student");
        }
        participants.put(studentId, enrollmentId);
    }

    public Map<String, String> getParticipants() {
        return Collections.unmodifiableMap(participants);
    }

    public boolean isGroupLesson() {
        return participants.size() > 1;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    @Override
    public String getKind() {
        return "LESSON";
    }
}
