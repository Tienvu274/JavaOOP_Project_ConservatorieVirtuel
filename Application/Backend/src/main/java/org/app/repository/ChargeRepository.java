package org.app.repository;

import org.app.database.DbTime;
import org.app.model.Charge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ChargeRepository {

    public void insert(Connection connection, Charge charge) throws SQLException {
        String sql = "INSERT INTO charge "
                + "(id, student_id, source_type, "
                + "enrollment_id, activity_id, description, "
                + "amount_cents, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, charge.getId());
            statement.setString(2, charge.getStudentId());
            statement.setString(3, charge.getSourceType());
            statement.setString(4, charge.getEnrollmentId());
            statement.setString(5, charge.getActivityId());
            statement.setString(6, charge.getDescription());
            statement.setLong(7, charge.getAmountCents());
            statement.setLong(8, DbTime.toEpochSeconds(charge.getCreatedAt()));
            if (statement.executeUpdate() != 1) {
                throw new SQLException("Charge was not saved.");
            }
        }
    }


}