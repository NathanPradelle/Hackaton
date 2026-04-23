package esgi.hackathon.wsd.dto;

import esgi.hackathon.wsd.enums.DriverStatus;

public record DriverDto(
    Long id,
    Long userId,
    String firstName,
    String lastName,
    String licenseNumber,
    DriverStatus status
) {}