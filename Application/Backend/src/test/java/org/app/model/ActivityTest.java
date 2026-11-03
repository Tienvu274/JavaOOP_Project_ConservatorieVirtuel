package org.app.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ActivityTest {
    private final LocalDateTime start = LocalDateTime.of(2026, 11, 5, 10, 0);

    private Rental rental() {
        return new Rental("RENT_01", "STU_01", Rental.ResourceType.ROOM, "RM_01", start, 60, 1500, Activity.Status.SCHEDULED, null);
    }

    @Test
    void overlappingTimesAreDetected() {
        assertTrue(rental().overlaps(start.plusMinutes(30), 60));
    }

    @Test
    void adjacentBookingsDoNotOverlap() {
        assertFalse(rental().overlaps(start.plusHours(1), 60));
        assertFalse(rental().overlaps(start.minusHours(1), 60));
    }

    @Test
    void completionBeforeEndIsRejected() {
        Rental activity = rental();
        assertThrows(IllegalStateException.class, () -> activity.complete(start.plusMinutes(59)));
        assertEquals(Activity.Status.SCHEDULED, activity.getStatus());
    }

    @Test
    void completionAtEndSucceedsOnlyOnce() {
        Rental activity = rental();
        activity.complete(start.plusHours(1));
        assertEquals(Activity.Status.COMPLETED, activity.getStatus());
        assertThrows(IllegalStateException.class, () -> activity.complete(start.plusHours(2)));
    }

    @Test
    void cancellationAtStartIsRejected() {
        Rental activity = rental();
        assertThrows(IllegalStateException.class, () -> activity.cancel(start));
        assertEquals(Activity.Status.SCHEDULED, activity.getStatus());
    }

    @Test
    void changingCopyDoesNotChangeOriginal() {
        Rental original = rental();
        Rental copied = new Rental(original);
        copied.cancel(start.minusHours(1));
        assertNotSame(original, copied);
        assertEquals(original.getId(), copied.getId());
        assertEquals(Activity.Status.SCHEDULED, original.getStatus());
        assertEquals(Activity.Status.CANCELLED, copied.getStatus());
    }

    @Test
    void mixedActivityCollectionUsesConcreteMethods() {
        Lesson lesson = new Lesson("LES_01", start, "TCH_01", "RM_02", "PIANO", false, List.of("PIA_01"), Activity.Status.SCHEDULED, null, List.of());
        Exam exam = new Exam("EX_01", "Piano exam", "PIANO", "RM_03", start, 60, 2, List.of(), Activity.Status.SCHEDULED, null, List.of());
        List<Activity> activities = List.of(lesson, exam, rental());
        String kinds = "";
        for (Activity activity : activities) kinds += activity.getKind() + ";";
        assertEquals("LESSON;EXAM;ROOM_RENTAL;", kinds);
    }
}
