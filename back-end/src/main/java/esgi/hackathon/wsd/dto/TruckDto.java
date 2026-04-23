package esgi.hackathon.wsd.dto;

import esgi.hackathon.wsd.enums.TruckStatus;

public record TruckDto(
    Long id,
    Long modelId,
    TruckStatus status,
    String licensePlate,
    Double currentFuelLevel
) {}
