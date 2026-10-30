package org.app.service;

import org.app.database.DbContext;
import org.app.database.DbTime;
import org.app.model.*;
import org.app.repository.ChargeRepository;
import org.app.repository.RentalRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class RentalService {

    private final RentalRepository rentalRepository;
    private final ChargeRepository chargeRepository;
    private final SchedulingService schedulingService;

    public RentalService(RentalRepository rentalRepository, ChargeRepository chargeRepository, SchedulingService schedulingService) {
        this.rentalRepository = rentalRepository;
        this.chargeRepository = chargeRepository;
        this.schedulingService = schedulingService;
    }

    public Rental createRental(String id, String studentId, Rental.ResourceType type, String resourceId, LocalDateTime start, int minutes, LocalDateTime now) throws SQLException {
        Rental rental = prepareRental(id, studentId, type, resourceId, start, minutes, now);

        try (Connection connection = DbContext.open()) {
            connection.setAutoCommit(false);

            try {
                rentalRepository.insert(connection, rental);

                Charge charge = new Charge(chargeRepository.nextId(connection), studentId, "ACTIVITY", null, id, "Rental: " + resourceId, rental.getPriceCents(), now);
                chargeRepository.insert(connection, charge);

                connection.commit();
                return rental;
            } catch (SQLException | RuntimeException error) {
                try { connection.rollback(); }
                catch (SQLException rollbackError) { error.addSuppressed(rollbackError); }
                throw error;
            }
        }
    }

    public Rental prepareRental(String id, String studentId, Rental.ResourceType type, String resourceId, LocalDateTime start, int minutes, LocalDateTime now) throws SQLException {
        if (type == null) throw new IllegalArgumentException("Resource type is required.");
        if (start == null || now == null) throw new IllegalArgumentException("Start and current time are required.");
        if (!start.isAfter(now)) throw new IllegalArgumentException("Rental must start in the future.");
        if (minutes <= 0) throw new IllegalArgumentException("Duration must be positive.");

        DbTime.toEpochSeconds(start);
        DbTime.toEpochSeconds(start.plusMinutes(minutes));

        boolean studentExists = false;
        for (Student student : rentalRepository.loadStudents()) {
            if (student.getId().equals(studentId)) {
                studentExists = true;
                break;
            }
        }
        if (!studentExists) throw new IllegalArgumentException("Student not found: " + studentId);

        long hourlyPrice = -1;

        if (type == Rental.ResourceType.ROOM) {
            for (Room room : rentalRepository.loadRooms()) {
                if (room.getId().equals(resourceId)) {
                    hourlyPrice = room.getHourlyPriceCents();
                    break;
                }
            }
        } else {
            for (Instrument instrument : rentalRepository.loadInstruments()) {
                if (instrument.getId().equals(resourceId)) {
                    hourlyPrice = instrument.getHourlyPriceCents();
                    break;
                }
            }
        }

        if (hourlyPrice < 0){
            throw new IllegalArgumentException("Resource not found: " + resourceId);
        }

        Rental rental = new Rental(id, studentId, type, resourceId, start, minutes, calculatePrice(hourlyPrice, minutes), Activity.Status.SCHEDULED, null);

        schedulingService.checkConflicts(rental, rentalRepository.loadActivities());
        return rental;
    }

    private long calculatePrice(long hourlyPrice, int minutes) {
        long total = Math.multiplyExact(hourlyPrice, minutes);
        long price = total / 60;
        if (total % 60 >= 30) price++;
        return price;
    }
}