package org.app.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class cExamTest {
    private final LocalDateTime start = LocalDateTime.of(2026, 11, 5, 10, 0);

    private Exam exam(int capacity) {
        return new Exam("EX_01", "Piano exam", "PIANO", "RM_01", start, 60, capacity, List.of("PIA_01"), Activity.Status.SCHEDULED, null, List.of());
    }

    @Test
    void registrationBeforeStartSucceeds() {
        Exam exam = exam(2);
        exam.register("STU_01", start.minusDays(1));
        assertEquals(1, exam.getRegistrations().size());
        assertEquals("STU_01", exam.getRegistrations().get(0).getStudentId());
    }

    @Test
    void duplicateRegistrationIsRejected() {
        Exam exam = exam(2);
        exam.register("STU_01", start.minusDays(1));
        assertThrows(IllegalStateException.class, () -> exam.register("STU_01", start.minusHours(1)));
        assertEquals(1, exam.getRegistrations().size());
    }

    @Test
    void fullExamRejectsAnotherStudent() {
        Exam exam = exam(1);
        exam.register("STU_01", start.minusDays(1));
        assertThrows(IllegalStateException.class, () -> exam.register("STU_02", start.minusHours(1)));
        assertEquals(1, exam.getRegistrations().size());
    }

    @Test
    void registrationAtStartIsClosed() {
        Exam exam = exam(2);
        assertThrows(IllegalStateException.class, () -> exam.register("STU_01", start));
        assertTrue(exam.getRegistrations().isEmpty());
    }

    @Test
    void resultBeforeCompletionIsRejected() {
        Exam exam = exam(2);
        exam.register("STU_01", start.minusDays(1));
        assertThrows(IllegalStateException.class, () -> exam.recordResult("STU_01", true, new BigDecimal("85"), start.plusHours(1)));
        assertNull(exam.getRegistrations().get(0).getResult());
    }

    @Test
    void zeroScoreAndMissingScoreRemainDifferent() {
        Exam exam = exam(2);
        exam.register("STU_01", start.minusDays(1));
        exam.register("STU_02", start.minusDays(1));
        exam.complete(start.plusHours(1));
        exam.recordResult("STU_01", false, BigDecimal.ZERO, start.plusHours(1));
        exam.recordResult("STU_02", true, null, start.plusHours(1));
        assertEquals(BigDecimal.ZERO, exam.getRegistrations().get(0).getResult().getScore());
        assertFalse(exam.getRegistrations().get(0).getResult().isPassed());
        assertNull(exam.getRegistrations().get(1).getResult().getScore());
        assertTrue(exam.getRegistrations().get(1).getResult().isPassed());
        assertThrows(IllegalStateException.class, () -> exam.recordResult("STU_01", true, BigDecimal.TEN, start.plusHours(2)));
    }

    @Test
    void unregisteredStudentCannotReceiveAResult() {
        Exam exam = exam(2);
        exam.complete(start.plusHours(1));
        assertThrows(IllegalStateException.class, () -> exam.recordResult("STU_01", true, BigDecimal.TEN, start.plusHours(1)));
    }
}
