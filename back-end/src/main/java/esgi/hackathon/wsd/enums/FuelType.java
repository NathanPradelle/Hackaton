package esgi.hackathon.wsd.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum FuelType {
    @JsonProperty("diesel") DIESEL,
    @JsonProperty("essence") GASOLINE,
    @JsonProperty("electrique") ELECTRIC,
    @JsonProperty("gnv") GNV
}
