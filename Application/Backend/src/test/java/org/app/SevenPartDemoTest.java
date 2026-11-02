package org.app;

import org.app.database.DbLoader;
import org.app.model.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@EnabledIfSystemProperty(named = "demo", matches = "true")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Execution(ExecutionMode.SAME_THREAD)
public class SevenPartDemoTest {
    private boolean previousPassed = true;

    @BeforeEach
    void stopAfterFailure() {
        Assumptions.assumeTrue(previousPassed, "Previous demo part failed. Check database before continuing.");
        previousPassed = false;
    }

    private void runPart(String file) throws Exception {
        ConsoleDemoRunner.run(file);
        previousPassed = true;
    }

    @Test @Order(1) @DisplayName("01 Registration and subscription")
    void registration() throws Exception { runPart("01-registration.txt"); }

    @Test @Order(2) @DisplayName("02 Payments and balance")
    void payments() throws Exception { runPart("02-payment.txt"); }

    @Test @Order(3) @DisplayName("03 Learning hours and scheduling")
    void scheduling() throws Exception { runPart("03-scheduling.txt"); }

    @Test @Order(4) @DisplayName("04 Teachers and instruments")
    void teachersAndInstruments() throws Exception { runPart("04-teachers-instruments.txt"); }

    @Test @Order(5) @DisplayName("05 Exams and results")
    void exams() throws Exception { runPart("05-exams-results.txt"); }

    @Test @Order(6) @DisplayName("06 Conflict rejection and late cancellation")
    void sbusinessRules() throws Exception { runPart("06-conflict-cancellation.txt"); }

    @Test @Order(7) @DisplayName("07 Polymorphism and copy constructor")
    void oop() throws Exception {
        List<String> ids = Files.readAllLines(Path.of("src/test/resources/demo/07-oop.txt"));
        assertEquals(3, ids.size(), "File must contain finite package ID, unlimited package ID, teacher ID.");
        List<CoursePackage> mixed = new ArrayList<>();
        List<CoursePackage> all = DbLoader.loadPackages();
        for (int i = 0; i < 2; i++) {
            String id = ids.get(i).trim();
            mixed.add(all.stream().filter(p -> p.getId().equals(id)).findFirst().orElseThrow());
        }
        assertFalse(mixed.get(0).isUnlimited());
        assertTrue(mixed.get(1).isUnlimited());
        StringBuilder output = new StringBuilder("===== 07 OOP =====\n");
        for (CoursePackage p : mixed) {
            CoursePackage copy = p.copy();
            assertNotSame(p, copy);
            assertEquals(p.getClass(), copy.getClass());
            output.append(p.getClass().getSimpleName()).append(" | unlimited=").append(p.isUnlimited())
                    .append(" | copy returns ").append(copy.getClass().getSimpleName()).append('\n');
        }
        Teacher original = DbLoader.loadTeachers().stream().filter(t -> t.getId().equals(ids.get(2).trim())).findFirst().orElseThrow();
        Teacher copy = new Teacher(original);
        int before = original.getAvailability().size();
        LocalDateTime start = LocalDateTime.of(2026, 12, 1, 8, 0);
        copy.addAvailability(new TimeSlot(start, start.plusHours(1)));
        assertNotSame(original, copy);
        assertEquals(before, original.getAvailability().size());
        assertEquals(before + 1, copy.getAvailability().size());
        assertEquals(original.getId(), copy.getId());
        original.getSpecializations().clear();
        assertTrue(original.canTeach("GUITAR"));
        output.append("Same object: false\nOriginal unchanged: true\nOriginal slots: ").append(before)
                .append("\nCopy slots: ").append(before + 1).append("\nNo database insert.\n");
        System.out.println(output);
        Path folder = Path.of("target/console-demo");
        Files.createDirectories(folder);
        Files.writeString(folder.resolve("07-oop-output.txt"), output);
        previousPassed = true;
    }
}
