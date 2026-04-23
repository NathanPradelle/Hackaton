package esgi.hackathon.wsd.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import esgi.hackathon.wsd.enums.TruckStatus;

public record TruckDto(
    Long id,
    @JsonProperty("modeleId") Long modelId,
    @JsonProperty("statut") TruckStatus status,
    @JsonProperty("plaqueImmatriculation") String licensePlate,
    @JsonProperty("quantiteEssence") Double currentFuelLevel,
    VehicleModelDto modele
) {}
