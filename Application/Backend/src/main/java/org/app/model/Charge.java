package org.app.model;

public record Charge(String id, String studentId, String description, long amountCents) {
}
