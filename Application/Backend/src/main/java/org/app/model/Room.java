package org.app.model;

public record Room(String id, String name, int capacity, long hourlyPriceCents) {
}
