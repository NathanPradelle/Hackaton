package esgi.hackathon.wsd.dto;

public record ItineraryDto(
    Long id,
    Long tripId,
    Double duration,
    String constraints,
    String gpsData
) {}
