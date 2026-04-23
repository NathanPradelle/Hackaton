package esgi.hackathon.wsd.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import esgi.hackathon.wsd.enums.DriverStatus;

public record DriverDto(
    Long id,
    Long userId,
    @JsonProperty("nom") String firstName,
    @JsonProperty("prenom") String lastName,
    @JsonProperty("numeroPermis") String licenseNumber,
    @JsonProperty("statut") DriverStatus status
) {}
