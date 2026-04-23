package esgi.hackathon.wsd.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import esgi.hackathon.wsd.enums.TripStatus;
import java.time.LocalDate;

public record TripDto(
    Long id,
    @JsonProperty("chauffeurId") Long driverId,
    @JsonProperty("camionId") Long truckId,
    LocalDate date,
    @JsonProperty("plageHoraire") String timeSlot,
    @JsonProperty("statut") TripStatus status
) {}
