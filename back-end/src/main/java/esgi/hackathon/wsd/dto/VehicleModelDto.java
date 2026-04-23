package esgi.hackathon.wsd.dto;

import esgi.hackathon.wsd.enums.FuelType;

public record VehicleModelDto(
    Long id,
    String brand,
    String modelName,
    Double capacity,
    Double fuelConsumption,
    FuelType fuelType,
    Double tankCapacity
) {}
