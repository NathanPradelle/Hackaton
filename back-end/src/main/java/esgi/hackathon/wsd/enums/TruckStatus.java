package esgi.hackathon.wsd.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TruckStatus {
    @JsonProperty("disponible") AVAILABLE,
    @JsonProperty("maintenance") IN_MAINTENANCE,
    @JsonProperty("enCours") BUSY,
    @JsonProperty("horsService") OUT_OF_SERVICE
}
