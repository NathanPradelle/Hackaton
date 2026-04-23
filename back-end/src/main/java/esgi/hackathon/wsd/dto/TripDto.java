package esgi.hackathon.wsd.dto;

import esgi.hackathon.wsd.enums.TripStatus;
import java.time.LocalDate;

public record TripDto(
    Long id,
    Long driverId,
    Long truckId,
    LocalDate date,
    String timeSlot,
    TripStatus status
) {}
