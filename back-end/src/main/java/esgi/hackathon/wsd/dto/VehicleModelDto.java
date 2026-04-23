package esgi.hackathon.wsd.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import esgi.hackathon.wsd.enums.FuelType;

public record VehicleModelDto(
    Long id,
    @JsonProperty("marque") String brand,
    @JsonProperty("nomModele") String modelName,
    @JsonProperty("capacite") Double capacity,
    @JsonProperty("consommationEssence") Double fuelConsumption,
    @JsonProperty("typeEssence") FuelType fuelType,
    @JsonProperty("capaciteReservoir") Double tankCapacity
) {}
