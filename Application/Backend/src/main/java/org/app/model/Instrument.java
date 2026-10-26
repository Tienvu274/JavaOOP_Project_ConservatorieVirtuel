package org.app.model;

public record Instrument(String id, String name, String type, long hourlyPriceCents) {
}
