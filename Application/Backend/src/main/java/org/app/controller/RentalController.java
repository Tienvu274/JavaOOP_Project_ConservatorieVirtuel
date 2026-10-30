package org.app.controller;

import org.app.database.DbTime;
import org.app.model.Rental;
import org.app.service.RentalService;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class RentalController {

    private final RentalService service;

    public RentalController(RentalService service) {
        this.service = service;
    }

    public Rental createRental(String id, String studentId, Rental.ResourceType type, String resourceId, LocalDateTime start, int minutes) throws SQLException {
        return service.createRental(id, studentId, type, resourceId, start, minutes, LocalDateTime.now(DbTime.ZONE).withNano(0));
    }
}