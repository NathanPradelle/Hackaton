package esgi.hackathon.wsd.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ItineraryDto(
    Long id,
    @JsonProperty("tourneeId") Long tripId,
    @JsonProperty("duree") Double duration,
    @JsonProperty("contrainte") String constraints,
    @JsonProperty("infoGps") String gpsData
) {}
