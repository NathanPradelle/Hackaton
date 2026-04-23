package esgi.hackathon.wsd.algorithm.dto;

/**
 * Représente un step dans la réponse VROOM (ORS /optimization).
 * type : "start" | "job" | "end"
 */
public record VroomStep(
    String type,
    Long jobId,         // non-null si type == "job"
    double latitude,
    double longitude,
    long arrivalUnix    // timestamp Unix (secondes)
) {}
