package esgi.hackathon.wsd.algorithm.dto;

import java.time.LocalDateTime;

public record Waypoint(
    double latitude,
    double longitude,
    WaypointType type,
    Long orderId,         // non-null si type == LIVRAISON
    String stationName,   // non-null si type == CARBURANT
    LocalDateTime eta
) {
    public static Waypoint depot(double lat, double lng, LocalDateTime eta) {
        return new Waypoint(lat, lng, WaypointType.DEPOT, null, null, eta);
    }

    public static Waypoint livraison(double lat, double lng, Long orderId, LocalDateTime eta) {
        return new Waypoint(lat, lng, WaypointType.LIVRAISON, orderId, null, eta);
    }

    public static Waypoint carburant(double lat, double lng, String stationName, LocalDateTime eta) {
        return new Waypoint(lat, lng, WaypointType.CARBURANT, null, stationName, eta);
    }
}
