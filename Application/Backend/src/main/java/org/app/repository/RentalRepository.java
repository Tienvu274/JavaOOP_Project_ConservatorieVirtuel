package org.app.repository;

import org.app.database.DbLoader;
import org.app.database.DbTime;
import org.app.model.*;

import java.sql.*;
import java.util.List;

public class RentalRepository {

    public List<Student> loadStudents() throws SQLException {
        return DbLoader.loadStudents(); }
    public List<Room> loadRooms() throws SQLException {
        return DbLoader.loadRooms(); }
    public List<Instrument> loadInstruments() throws SQLException {
        return DbLoader.loadInstruments(); }
    public List<Activity> loadActivities() throws SQLException {
        return DbLoader.loadActivities(); }

    public void insert(Connection connection, Rental rental) throws SQLException {
        String sql = "INSERT INTO activity "
                + "(id, kind, starts_at, duration_minutes, room_id, status, changed_at, renter_id, rental_price_cents) "
                + "VALUES (?, ?, ?, ?, ?, 'SCHEDULED', NULL, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, rental.getId());
            statement.setString(2, rental.getKind());
            statement.setLong(3, DbTime.toEpochSeconds(rental.getStart()));
            statement.setInt(4, rental.getDurationMinutes());
            statement.setString(5, rental.getResourceType() == Rental.ResourceType.ROOM ? rental.getResourceId() : null);
            statement.setString(6, rental.getStudentId());
            statement.setLong(7, rental.getPriceCents());
            statement.executeUpdate();
        }

        if (rental.getResourceType() == Rental.ResourceType.INSTRUMENT) {
            sql = "INSERT INTO activity_instrument (activity_id, instrument_id) VALUES (?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, rental.getId());
                statement.setString(2, rental.getResourceId());
                statement.executeUpdate();
            }
        }
    }
}