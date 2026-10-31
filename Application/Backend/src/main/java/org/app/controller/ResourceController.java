package org.app.controller;

import org.app.model.Room;
import org.app.service.ResourceService;

import java.sql.SQLException;
import java.util.List;

public class ResourceController {

    private final ResourceService service;

    public ResourceController(ResourceService service) {
        this.service = service;
    }

    public List<Room> getAllRooms() throws SQLException {
        return service.getAllRooms();
    }

    public Room getRoomById(String id) throws SQLException {
        return service.getRoomById(id);
    }

    public void createRoom(Room room) throws SQLException {
        service.createRoom(room);
    }

    public void updateRoom(Room room) throws SQLException {
        service.updateRoom(room);
    }
}
