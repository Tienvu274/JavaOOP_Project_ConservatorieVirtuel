package org.app;


import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

// Opt in explicitly: ordinary mvn test must not insert demo data.
class ConsoleDemoRunner {
    static String run(String file) throws Exception {
        Path source = Path.of("src/test/resources/demo", file);
        List<String> inputs = new ArrayList<>();
        List<String> expected = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Paris")).withSecond(0).withNano(0);
        LocalDateTime conflictStart = now.plusHours(4).plusMinutes(30);
        if (file.startsWith("06-")) {
            org.app.model.Activity lesson = org.app.database.DbLoader.loadActivities().stream()
                    .filter(a -> a.getId().equals("LES_LIVE_01")).findFirst().orElseThrow();
            assertEquals(org.app.model.Activity.Status.SCHEDULED, lesson.getStatus(), "Run part 03 first; lesson must still be scheduled.");
            assertTrue(lesson.getStart().isAfter(now), "Lesson has already started.");
            assertTrue(lesson.getStart().isBefore(now.plusHours(24)), "This demo requires cancellation within 24 hours.");
            conflictStart = lesson.getStart().plusMinutes(30);
        }
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (String line : Files.readAllLines(source, StandardCharsets.UTF_8)) {
            if (line.startsWith("# EXPECT: ")) expected.add(line.substring(10));
            else if (line.startsWith("# ERROR: ")) errors.add(line.substring(9));
            else if (!line.startsWith("#")) {
                line = line.replace("${EXTRA_START}", now.plusDays(7).withHour(8).withMinute(0).format(format));
                line = line.replace("${EXTRA_END}", now.plusDays(7).withHour(10).withMinute(0).format(format));
                line = line.replace("${TODAY}", now.toLocalDate().toString());
                line = line.replace("${UNTIL}", now.toLocalDate().plusDays(30).toString());
                line = line.replace("${SLOT_START}", now.minusHours(1).format(format));
                line = line.replace("${SLOT_END}", now.plusHours(24).format(format));
                line = line.replace("${LESSON_START}", now.plusHours(4).format(format));
                line = line.replace("${CONFLICT_START}", conflictStart.format(format));
                assertFalse(line.contains("${"), "Unknown variable: " + line);
                inputs.add(line);
            }
        }
        assertFalse(expected.isEmpty(), "Add at least one # EXPECT: line.");
        assertFalse(inputs.isEmpty(), "Input file is empty.");
        assertEquals("0", inputs.getLast(), "Last input must be 0 to exit Main.");

        Path folder = Path.of("target/console-demo");
        Files.createDirectories(folder);
        Path inputFile = folder.resolve(file.replace(".txt", "-input.txt"));
        Path outputFile = folder.resolve(file.replace(".txt", "-output.txt"));
        Files.writeString(inputFile, String.join("\n", inputs) + "\n", StandardCharsets.UTF_8);
        String java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        String classpath = System.getProperty("surefire.test.class.path", System.getProperty("java.class.path"));
        System.out.println("\n===== AUTOMATED CONSOLE DEMO: " + file + " =====");
        Process process = new ProcessBuilder(java, "-Dfile.encoding=UTF-8", "-cp", classpath, "org.app.ConsoleDemoLauncher")
                .redirectInput(inputFile.toFile()).redirectErrorStream(true).start();
        AtomicReference<IOException> logError = new AtomicReference<>();
        Thread reader = new Thread(() -> {
            try (InputStream stream = process.getInputStream(); OutputStream log = Files.newOutputStream(outputFile)) {
                byte[] buffer = new byte[1024];
                int count;
                while ((count = stream.read(buffer)) != -1) {
                    System.out.write(buffer, 0, count);
                    System.out.flush();
                    log.write(buffer, 0, count);
                }
            } catch (IOException error) { logError.set(error); }
        });
        reader.start();
        boolean finished;
        try {
            finished = process.waitFor(60, TimeUnit.SECONDS);
        } finally {
            if (process.isAlive()) { process.destroyForcibly(); process.waitFor(); }
            reader.join();
        }
        if (logError.get() != null) throw logError.get();
        String output = Files.readString(outputFile, StandardCharsets.UTF_8);
        System.out.println("Input: " + inputFile.toAbsolutePath());
        System.out.println("Output: " + outputFile.toAbsolutePath());
        assertTrue(finished, "Console exceeded 60 seconds. Check input order and database connection.");
        assertEquals(0, process.exitValue(), "Main exited with an error.");
        assertFalse(output.contains("[DB] Initialization failed"), "Database initialization failed.");
        assertFalse(output.contains("Unknown choice."), "Input no longer matches the console menu.");
        List<String> actualErrors = output.lines().filter(line -> line.contains("[ERROR]")).toList();
        assertEquals(errors.size(), actualErrors.size(), "Unexpected number of console errors: " + actualErrors);
        for (int i = 0; i < errors.size(); i++) assertTrue(actualErrors.get(i).contains(errors.get(i)), "Unexpected error: " + actualErrors.get(i));
        // This transcript includes the echoed input as well as application output.
        for (String text : expected) assertTrue(output.contains(text), "Expected output not found: " + text);
        return output;
    }
}
