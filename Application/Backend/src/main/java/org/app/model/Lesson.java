package org.app.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Lesson extends Activity {

    private final String teacherId;
    private final String roomId;
    private final String instrumentType;
    private final boolean groupLesson;

    private final List<String> instrumentIds = new ArrayList<>();
    private final List<LessonParticipation> participants = new ArrayList<>();

    public Lesson(String id, LocalDateTime start, String teacherId, String roomId, String instrumentType, boolean groupLesson, List<String> instrumentIds, Status status, LocalDateTime changedAt, List<LessonParticipation> participants) {
        super(id, start, 60, status, changedAt);
        this.teacherId = ModelChecks.id(teacherId);
        this.roomId = ModelChecks.id(roomId);
        this.instrumentType = ModelChecks.type(instrumentType);
        this.groupLesson = groupLesson;
        ModelChecks.require(instrumentIds != null && !instrumentIds.isEmpty(), "Lesson needs an instrument!");

        for (String instrumentId : instrumentIds) {
            instrumentId = ModelChecks.id(instrumentId);
            ModelChecks.require(!this.instrumentIds.contains(instrumentId), "Duplicate instrument!");
            this.instrumentIds.add(instrumentId);
        }
        for (LessonParticipation participation : participants) {
            addParticipant(participation);
        }
    }

    public Lesson(Lesson other) {
        this(other.getId(), other.getStart(), other.teacherId, other.roomId, other.instrumentType, other.groupLesson, other.instrumentIds, other.getStatus(), other.getChangedAt(), other.participants);
    }

    public void addParticipant(LessonParticipation value) {
        ModelChecks.require(getId().equals(value.getLessonId()), "Wrong lesson ID!");

        for (LessonParticipation existing : participants) {
            ModelChecks.require(!existing.getStudentId().equals(value.getStudentId()), "Duplicate student!");
        }
        ModelChecks.require(groupLesson || participants.isEmpty(), "Individual lesson accepts one student!");
        participants.add(new LessonParticipation(value));
    }

    public String getTeacherId() {
        return teacherId;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getInstrumentType() {
        return instrumentType;
    }

    public boolean isGroupLesson() {
        return groupLesson;
    }

    public List<String> getInstrumentIds() {
        return new ArrayList<>(instrumentIds);
    }

    public List<LessonParticipation> getParticipants() {
        List<LessonParticipation> result = new ArrayList<>();

        for (LessonParticipation participation : participants) {
            result.add(new LessonParticipation(participation));
        }
        return result;
    }

    @Override
    public String getKind() {
        return "LESSON";
    }

    @Override
    public Activity copy() {
        return new Lesson(this);
    }
}