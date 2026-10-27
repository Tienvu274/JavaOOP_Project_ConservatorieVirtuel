package org.app.model;

public class Room {

    private final String id;
    private final String name;
    private final int capacity;
    private final long hourlyPriceCents;

    public Room(String id, String name, int capacity, long hourlyPriceCents) {
        this.id = ModelChecks.id(id);
        this.name = ModelChecks.text(name, "Room name");
        ModelChecks.require(capacity > 0, "Capacity must be positive!");
        this.capacity = capacity;
        this.hourlyPriceCents = ModelChecks.price(hourlyPriceCents);
    }

    public Room(Room other) {
        this(other.id, other.name, other.capacity, other.hourlyPriceCents);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public long getHourlyPriceCents() {
        return hourlyPriceCents;
    }

    @Override
    public String toString() {
        return id + " - " + name + " | capacity=" + capacity;
    }
}
