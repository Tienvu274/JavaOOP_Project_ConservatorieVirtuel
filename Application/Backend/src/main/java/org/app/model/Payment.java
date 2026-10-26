package org.app.model;

import java.time.LocalDateTime;

public record Payment(String id, String studentId, long amountCents, String method, LocalDateTime paidAt) {
}
