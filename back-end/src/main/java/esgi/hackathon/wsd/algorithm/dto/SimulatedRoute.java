package esgi.hackathon.wsd.algorithm.dto;

import java.util.List;

public record SimulatedRoute(
    List<Waypoint> waypoints,
    double distanceTotaleKm,
    int dureeEstimeeMinutes,
    double coutCarburantEstime   // en euros
) {}
