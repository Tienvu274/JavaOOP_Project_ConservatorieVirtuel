package org.app.model;

public class Instrument {

    private final String id;
    private final String name;
    private final String type;
    private final long hourlyPriceCents;

    public Instrument(String id, String name, String type, long hourlyPriceCents) {
        this.id = ModelChecks.id(id);
        this.name = ModelChecks.text(name, "Instrument name");
        this.type = ModelChecks.type(type);
        this.hourlyPriceCents = ModelChecks.price(hourlyPriceCents);
    }

    public Instrument(Instrument other) {
        this(other.id, other.name, other.type, other.hourlyPriceCents);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public long getHourlyPriceCents() {
        return hourlyPriceCents;
    }

    @Override
    public String toString() {
        return id + " - " + name + " | " + type;
    }
}
