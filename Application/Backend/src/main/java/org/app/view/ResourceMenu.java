package org.app.view;

import org.app.controller.ResourceController;
import org.app.model.Room;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ResourceMenu {

    private final ResourceController controller;

    public ResourceMenu(ResourceController controller) {
        this.controller = controller;
    }
    public void listRooms() throws SQLException {
        List<Room> rooms = controller.getAllRooms();
        System.out.println("\nROOMS (" + rooms.size() + ")");
        if (rooms.isEmpty()) {
            System.out.println("No rooms in the database.");
            return;
        }
        for (Room room : rooms) {
            System.out.println(room + " | price=" + room.getHourlyPriceCents() + " cents/h");
        }
    }

    public void findRoom(Scanner input) throws SQLException {
        System.out.print("Room ID: ");
        Room room = controller.getRoomById(input.nextLine());
        System.out.println("\n-ROOM DETAIL-");
        System.out.println("ID:       " + room.getId());
        System.out.println("Name:     " + room.getName());
        System.out.println("Capacity: " + room.getCapacity());
        System.out.println("Price:    " + room.getHourlyPriceCents() + " cents/h");
    }

    public void createRoom(Scanner input) throws SQLException {
        System.out.println("\n-CREATE ROOM-");
        controller.createRoom(readRoomFromInput(input));
        System.out.println("Room created.");
    }

    public void updateRoom(Scanner input) throws SQLException {
        System.out.println("\n-UPDATE ROOM-");
        controller.updateRoom(readRoomFromInput(input));
        System.out.println("Room updated.");
    }

    private Room readRoomFromInput(Scanner input) {
        System.out.print("ID: ");
        String id = input.nextLine().trim();
        System.out.print("Name: ");
        String name = input.nextLine().trim();
        System.out.print("Capacity: ");
        int capacity = Integer.parseInt(input.nextLine().trim());
        System.out.print("Hourly price (cents): ");
        long price = Long.parseLong(input.nextLine().trim());
        return new Room(id, name, capacity, price);
    }
}
