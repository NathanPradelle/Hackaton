package esgi.hackathon.wsd.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DriverStatus {
    @JsonProperty("disponible") AVAILABLE,
    @JsonProperty("enCours") ON_TRIP,
    @JsonProperty("pause") PAUSE,
    @JsonProperty("indisponible") OFF_DUTY
}
