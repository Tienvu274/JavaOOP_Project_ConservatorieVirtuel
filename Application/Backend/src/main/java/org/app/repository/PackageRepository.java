package org.app.repository;

import org.app.database.DbContext;
import org.app.model.CoursePackage;
import org.app.model.FinitePackage;
import org.app.model.UnlimitedPackage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class PackageRepository {

    public void insert(CoursePackage coursePackage) throws SQLException {
        String sql = "INSERT INTO package_offer "
                + "(id, name, price_cents, unlimited, lesson_count) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DbContext.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, coursePackage.getId());
                statement.setString(2, coursePackage.getName());
                statement.setLong(3, coursePackage.getPriceCents());
                statement.setBoolean(4, coursePackage.isUnlimited());

                if (coursePackage.isUnlimited()) {
                    statement.setNull(5, Types.INTEGER);
                } else {
                    statement.setInt(5, coursePackage.getLessonLimit());
                }

            int affectedRows = statement.executeUpdate();

            if (affectedRows != 1) {
                throw new SQLException("Package was not saved.");
            }
        }
    }

    public List<CoursePackage> findAll() throws SQLException {
        List<CoursePackage> packages = new ArrayList<>();
        String sql = "SELECT id, name, price_cents, "
                + "unlimited, lesson_count "
                + "FROM package_offer ORDER BY id";

        try (Connection connection = DbContext.open();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    String id = result.getString("id");
                    String name = result.getString("name");
                    long priceCents = result.getLong("price_cents");
                    boolean unlimited = result.getBoolean("unlimited");
                    CoursePackage coursePackage;

                    if (unlimited) {
                        coursePackage = new UnlimitedPackage(id, name, priceCents);
                    } else {
                        int lessonCount = result.getInt("lesson_count");
                        coursePackage = new FinitePackage(id, name, priceCents, lessonCount);
                    }
                    packages.add(coursePackage);
                }
            }
        return packages;
    }
}