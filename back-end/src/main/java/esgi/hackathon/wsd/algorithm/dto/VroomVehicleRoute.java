package esgi.hackathon.wsd.algorithm.dto;

import java.util.List;

/**
 * Tournée d'un véhicule dans la réponse VROOM.
 */
public record VroomVehicleRoute(
    long vehicleId,         // correspond à truck.id
    List<VroomStep> steps,
    double distanceMetres,
    long durationSeconds
) {}
