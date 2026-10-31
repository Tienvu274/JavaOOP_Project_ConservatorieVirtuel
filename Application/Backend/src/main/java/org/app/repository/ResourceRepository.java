package org.app.repository;

import org.app.model.Room;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResourceRepository {

    //T05
    public List<Room> findAllRooms(Connection connection) throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM room ORDER BY id";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet row = statement.executeQuery()) {
            while (row.next()) {
                rooms.add(readRoom(row));
            }
        }
        return rooms;
    }

    public Room findRoomById(Connection connection, String id) throws SQLException {
        String sql = "SELECT * FROM room WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            try (ResultSet row = statement.executeQuery()) {
                if (row.next()) {
                    return readRoom(row);
                }
                return null;
            }
        }
    }

    private Room readRoom(ResultSet row) throws SQLException {
        return new Room(
                row.getString("id"),
                row.getString("name"),
                row.getInt("capacity"),
                row.getLong("hourly_price_cents")
        );
    }

    public void insertRoom(Connection connection, Room room) throws SQLException {
        String sql = "INSERT INTO room (id, name, capacity, hourly_price_cents) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, room.getId());
            statement.setString(2, room.getName());
            statement.setInt(3, room.getCapacity());
            statement.setLong(4, room.getHourlyPriceCents());
            if (statement.executeUpdate() != 1) {
                throw new SQLException("Room was not saved.");
            }
        }
    }

    public int updateRoomBasic(Connection connection, Room room) throws SQLException {
        String sql = "UPDATE room SET name = ?, capacity = ?, hourly_price_cents = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, room.getName());
            statement.setInt(2, room.getCapacity());
            statement.setLong(3, room.getHourlyPriceCents());
            statement.setString(4, room.getId());    // ID không đổi (N08)
            return statement.executeUpdate();
        }
    }
}
