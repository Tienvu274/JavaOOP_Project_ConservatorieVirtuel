package org.app.database;

import org.app.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class DbLoader {

    private DbLoader() {
    }

    public static boolean testConnection() throws SQLException {
        try (
                Connection connection = DbContext.open();
                Statement statement = connection.createStatement();
                ResultSet result =
                        statement.executeQuery("SELECT 1")
        ) {
            return result.next()
                    && result.getInt(1) == 1;
        }
    }

    public static String databaseInfo() throws SQLException {
        try (Connection connection = DbContext.open()) {
            DatabaseMetaData metadata = connection.getMetaData();

            return metadata.getDatabaseProductName()
                    + " "
                    + metadata.getDatabaseProductVersion()
                    + " | database="
                    + connection.getCatalog()
                    + " | user="
                    + metadata.getUserName();
        }
    }

    public static List<String> loadTableCounts() throws SQLException {
        List<String> result = new ArrayList<>();
        try (Connection connection = DbContext.open()) {
            for (String table : DbInitializer.getBusinessTables()) {
                try (
                        Statement statement =
                                connection.createStatement();

                        ResultSet row = statement.executeQuery(
                                "SELECT COUNT(*) FROM " + table
                        )
                ) {
                    row.next();
                    result.add(
                            table + ": " + row.getLong(1)
                    );
                }
            }
        }
        return result;
    }

    private static List<String> types(Connection connection, String table, String idColumn, String id) throws SQLException {
        List<String> result = new ArrayList<>();
        String query =
                "SELECT instrument_type FROM "
                        + table
                        + " WHERE "
                        + idColumn
                        + "=? ORDER BY instrument_type";

        try (
                PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setString(1, id);
            try (ResultSet row = statement.executeQuery()) {
                while (row.next()) {
                    result.add(
                            row.getString("instrument_type")
                    );
                }
            }
        }
        return result;
    }

    public static List<Student> loadStudents() throws SQLException {
        List<Student> result = new ArrayList<>();
        try (
                Connection connection = DbContext.open();
                Statement statement = connection.createStatement();
                ResultSet row = statement.executeQuery(
                        "SELECT * FROM student ORDER BY id"
                )
        ) {
            while (row.next()) {
                result.add(
                        new Student(
                                row.getString("id"),
                                row.getString("first_name"),
                                row.getString("last_name"),
                                row.getString("address"),
                                row.getDate("birth_date").toLocalDate(),
                                row.getString("phone"),
                                row.getString("email"),
                                row.getString("level"),
                                types(
                                        connection,
                                        "student_preference",
                                        "student_id",
                                        row.getString("id")
                                )
                        )
                );
            }
        }
        return result;
    }

    public static List<Teacher> loadTeachers() throws SQLException {
        List<Teacher> result = new ArrayList<>();
        try (
                Connection connection = DbContext.open();
                Statement statement = connection.createStatement();
                ResultSet row = statement.executeQuery("SELECT * FROM teacher ORDER BY id")) {
            while (row.next()) {
                Teacher teacher = new Teacher(
                        row.getString("id"),
                        row.getString("first_name"),
                        row.getString("last_name"),
                        row.getLong("hourly_rate_cents"),
                        row.getString("qualifications")
                );
                List<String> specializations = types(
                        connection,
                        "teacher_specialization",
                        "teacher_id",
                        teacher.getId()
                );
                for (String type : specializations) {
                    teacher.addSpecialization(type);
                }
                try (
                        PreparedStatement slots =
                                connection.prepareStatement(
                                        "SELECT * FROM teacher_availability "
                                                + "WHERE teacher_id=? "
                                                + "ORDER BY slot_number"
                                )
                ) {
                    slots.setString(1, teacher.getId());
                    try (ResultSet slot = slots.executeQuery()) {
                        while (slot.next()) {
                            teacher.addAvailability(
                                    new TimeSlot(
                                            DbTime.read(slot, "starts_at"),
                                            DbTime.read(slot, "ends_at")
                                    )
                            );
                        }
                    }
                }
                result.add(teacher);
            }
        }
        return result;
    }

    public static List<Room> loadRooms() throws SQLException {
        List<Room> result = new ArrayList<>();
        try (
                Connection connection = DbContext.open();
                Statement statement = connection.createStatement();
                ResultSet row = statement.executeQuery("SELECT * FROM room ORDER BY id")) {
            while (row.next()) {
                result.add(
                        new Room(
                                row.getString("id"),
                                row.getString("name"),
                                row.getInt("capacity"),
                                row.getLong("hourly_price_cents")
                        )
                );
            }
        }
        return result;
    }

    public static List<Instrument> loadInstruments() throws SQLException {
        List<Instrument> result = new ArrayList<>();
        try (
                Connection connection = DbContext.open();
                Statement statement = connection.createStatement();
                ResultSet row = statement.executeQuery(
                        "SELECT * FROM instrument ORDER BY id"
                )
        ) {
            while (row.next()) {
                result.add(
                        new Instrument(
                                row.getString("id"),
                                row.getString("name"),
                                row.getString("instrument_type"),
                                row.getLong("hourly_price_cents")
                        )
                );
            }
        }
        return result;
    }

    private static CoursePackage pack(String id, String name, long price, boolean unlimited, int count) {
        if (unlimited) {
            return new UnlimitedPackage(id, name, price);
        }
        return new FinitePackage(id, name, price, count);
    }

    public static List<CoursePackage> loadPackages() throws SQLException {
        List<CoursePackage> result = new ArrayList<>();
        try (
                Connection connection = DbContext.open();
                Statement statement = connection.createStatement();
                ResultSet row = statement.executeQuery(
                        "SELECT * FROM package_offer ORDER BY id"
                )
        ) {
            while (row.next()) {
                result.add(
                        pack(
                                row.getString("id"),
                                row.getString("name"),
                                row.getLong("price_cents"),
                                row.getBoolean("unlimited"),
                                row.getInt("lesson_count")
                        )
                );
            }
        }
        return result;
    }

    public static List<Enrollment> loadEnrollments() throws SQLException {
        List<Enrollment> result = new ArrayList<>();
        try (
                Connection connection = DbContext.open();
                Statement statement = connection.createStatement();
                ResultSet row = statement.executeQuery(
                        "SELECT * FROM enrollment ORDER BY id"
                )
        ) {
            while (row.next()) {
                CoursePackage snapshot = pack(
                        row.getString("package_id"),
                        row.getString("package_name"),
                        row.getLong("price_cents"),
                        row.getBoolean("unlimited"),
                        row.getInt("lesson_count")
                );
                result.add(
                        new Enrollment(
                                row.getString("id"),
                                row.getString("student_id"),
                                snapshot,
                                row.getDate("valid_from").toLocalDate(),
                                row.getDate("valid_through").toLocalDate(),
                                DbTime.read(row, "purchased_at")
                        )
                );
            }
        }
        return result;
    }

    public static List<LessonParticipation> loadParticipations() throws SQLException {
        try (Connection connection = DbContext.open()) {
            return participations(connection, null);
        }
    }

    private static List<LessonParticipation> participations(Connection connection, String lessonId) throws SQLException {
        List<LessonParticipation> result = new ArrayList<>();
        String query = "SELECT * FROM lesson_participation";
        if (lessonId != null) {
            query += " WHERE lesson_id=?";
        }
        query += " ORDER BY lesson_id,student_id";
        try (
                PreparedStatement statement =
                        connection.prepareStatement(query)
        ) {
            if (lessonId != null) {
                statement.setString(1, lessonId);
            }
            try (ResultSet row = statement.executeQuery()) {
                while (row.next()) {
                    result.add(
                            new LessonParticipation(
                                    row.getString("lesson_id"),
                                    row.getString("student_id"),
                                    row.getString("enrollment_id"),
                                    row.getInt("minutes"),
                                    LessonParticipation.State.valueOf(
                                            row.getString("state")
                                    ),
                                    DbTime.read(row, "reserved_at"),
                                    DbTime.read(row, "settled_at")
                            )
                    );
                }
            }
        }
        return result;
    }

    private static List<String> instruments(Connection connection, String activityId) throws SQLException {
        List<String> result = new ArrayList<>();
        try (
                PreparedStatement statement =
                        connection.prepareStatement(
                                "SELECT instrument_id "
                                        + "FROM activity_instrument "
                                        + "WHERE activity_id=? "
                                        + "ORDER BY instrument_id"
                        )
        ) {
            statement.setString(1, activityId);
            try (ResultSet row = statement.executeQuery()) {
                while (row.next()) {
                    result.add(
                            row.getString("instrument_id")
                    );
                }
            }
        }
        return result;
    }

    private static ExamResult examResult(Connection connection, String examId, String studentId) throws SQLException {
        try (
                PreparedStatement statement =
                        connection.prepareStatement(
                                "SELECT * FROM exam_result "
                                        + "WHERE exam_id=? "
                                        + "AND student_id=?"
                        )
        ) {
            statement.setString(1, examId);
            statement.setString(2, studentId);

            try (ResultSet row = statement.executeQuery()) {
                if (row.next()) {
                    return new ExamResult(
                            row.getBoolean("passed"),
                            row.getBigDecimal("score"),
                            DbTime.read(row, "recorded_at")
                    );
                }
            }
        }
        return null;
    }

    public static List<ExamRegistration> loadExamRegistrations() throws SQLException {
        try (Connection connection = DbContext.open()) {
            return registrations(connection, null);
        }
    }

    private static List<ExamRegistration> registrations(Connection connection, String examId) throws SQLException {
        List<ExamRegistration> result = new ArrayList<>();
        String query = "SELECT * FROM exam_registration";
        if (examId != null) {
            query += " WHERE exam_id=?";
        }
        query += " ORDER BY exam_id,student_id";
        try (
                PreparedStatement statement =
                        connection.prepareStatement(query)
        ) {
            if (examId != null) {
                statement.setString(1, examId);
            }
            try (ResultSet row = statement.executeQuery()) {
                while (row.next()) {
                    String exam = row.getString("exam_id");
                    String student = row.getString("student_id");
                    result.add(
                            new ExamRegistration(
                                    exam,
                                    student,
                                    DbTime.read(row, "registered_at"),
                                    examResult(
                                            connection,
                                            exam,
                                            student
                                    )
                            )
                    );
                }
            }
        }
        return result;
    }

    public static List<Activity> loadActivities() throws SQLException {
        List<Activity> result = new ArrayList<>();
        try (
                Connection connection = DbContext.open();
                Statement statement = connection.createStatement();
                ResultSet row = statement.executeQuery(
                        "SELECT * FROM activity ORDER BY starts_at,id"
                )
        ) {
            while (row.next()) {
                String id = row.getString("id");
                String kind = row.getString("kind");

                Activity.Status status =
                        Activity.Status.valueOf(
                                row.getString("status")
                        );
                List<String> instrumentIds =
                        instruments(connection, id);
                Activity activity;
                if (kind.equals("LESSON")) {
                    activity = new Lesson(
                            id,
                            DbTime.read(row, "starts_at"),
                            row.getString("teacher_id"),
                            row.getString("room_id"),
                            row.getString("instrument_type"),
                            row.getBoolean("group_lesson"),
                            instrumentIds,
                            status,
                            DbTime.read(row, "changed_at"),
                            participations(connection, id)
                    );
                } else if (kind.equals("EXAM")) {
                    activity = new Exam(
                            id,
                            row.getString("exam_name"),
                            row.getString("instrument_type"),
                            row.getString("room_id"),
                            DbTime.read(row, "starts_at"),
                            row.getInt("duration_minutes"),
                            row.getInt("exam_capacity"),
                            instrumentIds,
                            status,
                            DbTime.read(row, "changed_at"),
                            registrations(connection, id)
                    );
                } else if (kind.equals("ROOM_RENTAL") || kind.equals("INSTRUMENT_RENTAL")) {
                    Rental.ResourceType resourceType = Rental.ResourceType.ROOM;
                    String resourceId = row.getString("room_id");
                    if (kind.equals("INSTRUMENT_RENTAL")) {
                        if (instrumentIds.size() != 1) {
                            throw new SQLException(
                                    "Instrument rental must reference "
                                            + "one instrument: " + id
                            );
                        }
                        resourceType = Rental.ResourceType.INSTRUMENT;

                        resourceId = instrumentIds.get(0);
                    }

                    activity = new Rental(
                            id,
                            row.getString("renter_id"),
                            resourceType,
                            resourceId,
                            DbTime.read(row, "starts_at"),
                            row.getInt("duration_minutes"),
                            row.getLong("rental_price_cents"),
                            status,
                            DbTime.read(row, "changed_at")
                    );

                } else {
                    throw new SQLException("Unknown activity kind: " + kind
                    );
                }
                result.add(activity);
            }
        }
        return result;
    }

    public static List<Charge> loadCharges() throws SQLException {
        List<Charge> result = new ArrayList<>();
        try (
                Connection connection = DbContext.open();
                Statement statement = connection.createStatement();
                ResultSet row = statement.executeQuery("SELECT * FROM charge ORDER BY id")) {
            while (row.next()) {
                result.add(
                        new Charge(
                                row.getString("id"),
                                row.getString("student_id"),
                                row.getString("source_type"),
                                row.getString("enrollment_id"),
                                row.getString("activity_id"),
                                row.getString("description"),
                                row.getLong("amount_cents"),
                                DbTime.read(row, "created_at")
                        )
                );
            }
        }
        return result;
    }

    public static List<Payment> loadPayments() throws SQLException {
        List<Payment> result = new ArrayList<>();
        try (
                Connection connection = DbContext.open();
                Statement statement = connection.createStatement();
                ResultSet row = statement.executeQuery("SELECT * FROM payment ORDER BY id")) {
            while (row.next()) {
                result.add(
                        new Payment(
                                row.getString("id"),
                                row.getString("student_id"),
                                row.getLong("amount_cents"),
                                row.getString("method"),
                                DbTime.read(row, "paid_at")
                        )
                );
            }
        }
        return result;
    }
}