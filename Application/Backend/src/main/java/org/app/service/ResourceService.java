package org.app.service;

import org.app.database.DbContext;
import org.app.model.Instrument;
import org.app.model.Room;
import org.app.repository.ResourceRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ResourceService {

    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public List<Room> getAllRooms() throws SQLException {
        try (Connection connection = DbContext.open()) {
            return resourceRepository.findAllRooms(connection);
        }
    }

    public Room getRoomById(String id) throws SQLException {
        requireValidId(id);
        try (Connection connection = DbContext.open()) {
            Room room = resourceRepository.findRoomById(connection, id.trim());
            if (room == null) {
                throw new IllegalArgumentException("Room not found: " + id.trim());
            }
            return room;
        }
    }

    public void createRoom(Room room) throws SQLException {
        if (room == null) {
            throw new IllegalArgumentException("Please fill in");
        }
        try (Connection connection = DbContext.open()) {
            try {
                resourceRepository.insertRoom(connection, room);
            } catch (SQLException error) {
                if (error.getErrorCode() == 1062) {
                    throw new IllegalArgumentException("Room ID already exists: " + room.getId(), error);
                }
                throw error;
            }
        }
    }

    public void updateRoom(Room room) throws SQLException {
        if (room == null) {
            throw new IllegalArgumentException("Room is required");
        }
        try (Connection connection = DbContext.open()) {
            int rows = resourceRepository.updateRoomBasic(connection, room);
            if (rows == 0) {
                throw new IllegalArgumentException("Room not found: " + room.getId());
            }
        }
    }

    private void requireValidId(String id) {
        if (id == null || !id.trim().matches("[A-Za-z0-9_-]{1,40}")) {
            throw new IllegalArgumentException("Invalid room ID");
        }
    }
    //T06
    public List<Instrument> getAllInstruments() throws SQLException {
        try (Connection connection = DbContext.open()) {
            return resourceRepository.findAllInstruments(connection);
        }
    }

    public Instrument getInstrumentById(String id) throws SQLException {
        requireValidId(id);
        try (Connection connection = DbContext.open()) {
            Instrument instrument = resourceRepository.findInstrumentById(connection, id.trim());
            if (instrument == null) {
                throw new IllegalArgumentException("Instrument not found: " + id.trim());
            }
            return instrument;
        }
    }

    public void createInstrument(Instrument instrument) throws SQLException {
        if (instrument == null) {
            throw new IllegalArgumentException("Instrument is required.");
        }
        try (Connection connection = DbContext.open()) {
            try {
                resourceRepository.insertInstrument(connection, instrument);
            } catch (SQLException error) {
                if (error.getErrorCode() == 1062) {
                    throw new IllegalArgumentException("Instrument ID already exists: " + instrument.getId(), error);
                }
                throw error;
            }
        }
    }

    public void updateInstrument(Instrument instrument) throws SQLException {
        if (instrument == null) {
            throw new IllegalArgumentException("Instrument is required.");
        }
        try (Connection connection = DbContext.open()) {
            int rows = resourceRepository.updateInstrumentBasic(connection, instrument);
            if (rows == 0) {
                throw new IllegalArgumentException("Instrument not found: " + instrument.getId());
            }
        }
    }

}
