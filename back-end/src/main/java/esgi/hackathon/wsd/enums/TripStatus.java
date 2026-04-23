package esgi.hackathon.wsd.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TripStatus {
    @JsonProperty("planifiee") PLANNED,
    @JsonProperty("enCours") IN_PROGRESS,
    @JsonProperty("terminee") COMPLETED,
    @JsonProperty("annulee") CANCELLED
}
