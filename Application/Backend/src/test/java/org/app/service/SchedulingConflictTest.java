package org.app.service;

import org.app.model.*;
import org.app.repository.ChargeRepository;
import org.app.repository.LessonRepository;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SchedulingConflictTest {
    private final LocalDateTime start = LocalDateTime.of(2026, 11, 5, 10, 0);
    // checkConflicts uses only the supplied list; these repositories never open a connection.
    private final SchedulingService service = new SchedulingService(new LessonRepository(), new ChargeRepository());

    private Lesson lesson(String id, String teacher, String room, String instrument, String student, LocalDateTime time) {
        LessonParticipation participant = new LessonParticipation(id, student, null, 60, LessonParticipation.State.RESERVED, start.minusDays(1), null);
        return new Lesson(id, time, teacher, room, "PIANO", false, List.of(instrument), Activity.Status.SCHEDULED, null, List.of(participant));
    }

    private Lesson existing() { return lesson("LES_01", "TCH_01", "RM_01", "PIA_01", "STU_01", start); }

    @Test
    void occupiedRoomIsRejected() {
        Lesson candidate = lesson("LES_02", "TCH_02", "RM_01", "PIA_02", "STU_02", start.plusMinutes(30));
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> service.checkConflicts(candidate, List.of(existing())));
        assertTrue(error.getMessage().startsWith("Room conflict"));
    }

    @Test
    void occupiedTeacherIsRejected() {
        Lesson candidate = lesson("LES_02", "TCH_01", "RM_02", "PIA_02", "STU_02", start);
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> service.checkConflicts(candidate, List.of(existing())));
        assertTrue(error.getMessage().startsWith("Teacher conflict"));
    }

    @Test
    void occupiedInstrumentIsRejected() {
        Lesson candidate = lesson("LES_02", "TCH_02", "RM_02", "PIA_01", "STU_02", start);
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> service.checkConflicts(candidate, List.of(existing())));
        assertTrue(error.getMessage().startsWith("Instrument conflict"));
    }

    @Test
    void busyStudentIsRejected() {
        Lesson candidate = lesson("LES_02", "TCH_02", "RM_02", "PIA_02", "STU_01", start);
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> service.checkConflicts(candidate, List.of(existing())));
        assertTrue(error.getMessage().startsWith("Student conflict"));
    }

    @Test
    void adjacentLessonIsAllowed() {
        Lesson candidate = lesson("LES_02", "TCH_01", "RM_01", "PIA_01", "STU_01", start.plusHours(1));
        assertDoesNotThrow(() -> service.checkConflicts(candidate, List.of(existing())));
    }

    @Test
    void cancelledLessonDoesNotBlockResources() {
        Lesson cancelled = existing();
        cancelled.cancel(start.minusHours(1));
        Lesson candidate = lesson("LES_02", "TCH_01", "RM_01", "PIA_01", "STU_01", start);
        assertDoesNotThrow(() -> service.checkConflicts(candidate, List.of(cancelled)));
    }

    @Test
    void examRoomAlsoBlocksALesson() {
        Exam exam = new Exam("EX_01", "Piano exam", "PIANO", "RM_01", start, 60, 2, List.of("PIA_02"), Activity.Status.SCHEDULED, null, List.of());
        assertThrows(IllegalArgumentException.class, () -> service.checkConflicts(existing(), List.of(exam)));
    }

    @Test
    void instrumentRentalAlsoBlocksALesson() {
        Rental rental = new Rental("RENT_01", "STU_02", Rental.ResourceType.INSTRUMENT, "PIA_01", start, 60, 600, Activity.Status.SCHEDULED, null);
        assertThrows(IllegalArgumentException.class, () -> service.checkConflicts(existing(), List.of(rental)));
    }
}
