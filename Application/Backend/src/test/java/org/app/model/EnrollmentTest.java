package org.app.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class EnrollmentTest {
    private final LocalDate from = LocalDate.of(2026, 11, 1);
    private final LocalDate until = LocalDate.of(2026, 11, 30);
    private final LocalDateTime booked = LocalDateTime.of(2026, 11, 2, 9, 0);

    private Enrollment enrollment() {
        return new Enrollment("ENR_01", "STU_01", new FinitePackage("PKG_05", "Piano 5 lessons", 10000, 5), from, until, from.atStartOfDay());
    }

    private LessonParticipation reserved() {
        return new LessonParticipation("LES_01", "STU_01", "ENR_01", 60, LessonParticipation.State.RESERVED, booked, null);
    }

    @Test
    void newPackageHasFullAllowance() {
        assertEquals(300, enrollment().remainingMinutes(List.of()).intValue());
    }

    @Test
    void reservationReducesAvailableMinutes() {
        assertEquals(240, enrollment().availableMinutes(from.plusDays(2), List.of(reserved())).intValue());
    }

    @Test
    void completionDoesNotDeductTheReservationTwice() {
        LessonParticipation consumed = reserved().settle(LessonParticipation.State.CONSUMED, booked.plusDays(1));
        assertEquals(enrollment().remainingMinutes(List.of(reserved())), enrollment().remainingMinutes(List.of(consumed)));
        assertEquals(240, enrollment().remainingMinutes(List.of(consumed)).intValue());
    }

    @Test
    void releasedReservationReturnsMinutes() {
        LessonParticipation released = reserved().settle(LessonParticipation.State.RELEASED, booked.plusHours(1));
        assertEquals(300, enrollment().remainingMinutes(List.of(released)).intValue());
    }

    @Test
    void expiredPackageHasNoAvailableMinutes() {
        assertEquals(0, enrollment().availableMinutes(until.plusDays(1), List.of()).intValue());
    }

    @Test
    void validityIncludesBothBoundaryDates() {
        assertTrue(enrollment().isValidOn(from));
        assertTrue(enrollment().isValidOn(until));
        assertEquals(0, enrollment().availableMinutes(from.minusDays(1), List.of()).intValue());
    }

    @Test
    void unlimitedPackageUsesNullForUnlimitedMinutes() {
        Enrollment unlimited = new Enrollment("ENR_02", "STU_02", new UnlimitedPackage("PKG_UNL", "Unlimited", 20000), from, until, from.atStartOfDay());
        assertNull(unlimited.availableMinutes(from, List.of()));
        assertEquals(0, unlimited.availableMinutes(until.plusDays(1), List.of()).intValue());
    }

    @Test
    void groupParticipantsUseTheirOwnEnrollmentMinutes() {
        Enrollment second = new Enrollment("ENR_02", "STU_02", new FinitePackage("PKG_05", "Piano 5 lessons", 10000, 5), from, until, booked);
        LessonParticipation other = new LessonParticipation("LES_01", "STU_02", "ENR_02", 60, LessonParticipation.State.RESERVED, booked, null);
        List<LessonParticipation> history = List.of(reserved(), other);
        assertEquals(240, enrollment().remainingMinutes(history).intValue());
        assertEquals(240, second.remainingMinutes(history).intValue());
    }

    @Test
    void negativePackagePriceIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new FinitePackage("PKG_05", "Piano", -1, 5));
    }
}
