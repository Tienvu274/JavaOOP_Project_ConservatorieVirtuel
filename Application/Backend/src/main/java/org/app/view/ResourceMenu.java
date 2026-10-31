package org.app.view;

import org.app.controller.ResourceController;
import org.app.model.Instrument;
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
    //T06
    public void listInstruments() throws SQLException {
        List<Instrument> instruments = controller.getAllInstruments();
        System.out.println("\nINSTRUMENTS (" + instruments.size() + ")");
        if (instruments.isEmpty()) {
            System.out.println("No instruments in the database.");
            return;
        }
        for (Instrument instrument : instruments) {
            System.out.println(instrument + " | price=" + instrument.getHourlyPriceCents() + " cents/h");
        }
    }

    public void findInstrument(Scanner input) throws SQLException {
        System.out.print("Instrument ID: ");
        Instrument instrument = controller.getInstrumentById(input.nextLine());
        System.out.println("\n-INSTRUMENT DETAIL-");
        System.out.println("ID:    " + instrument.getId());
        System.out.println("Name:  " + instrument.getName());
        System.out.println("Type:  " + instrument.getType());
        System.out.println("Price: " + instrument.getHourlyPriceCents() + " cents/h");
    }

    public void createInstrument(Scanner input) throws SQLException {
        System.out.println("\n-CREATE INSTRUMENT-");
        controller.createInstrument(readInstrumentFromInput(input));
        System.out.println("Instrument created.");
    }

    public void updateInstrument(Scanner input) throws SQLException {
        System.out.println("\n-UPDATE INSTRUMENT-");
        controller.updateInstrument(readInstrumentFromInput(input));
        System.out.println("Instrument updated.");
    }

    private Instrument readInstrumentFromInput(Scanner input) {
        System.out.print("ID: ");
        String id = input.nextLine().trim();
        System.out.print("Name: ");
        String name = input.nextLine().trim();
        System.out.print("Type (PIANO/GUITAR/VIOLIN/FLUTE/DRUMS): ");
        String type = input.nextLine().trim();
        System.out.print("Hourly price (cents): ");
        long price = Long.parseLong(input.nextLine().trim());
        return new Instrument(id, name, type, price);
    }

}
