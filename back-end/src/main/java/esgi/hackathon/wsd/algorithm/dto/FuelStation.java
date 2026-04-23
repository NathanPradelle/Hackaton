package esgi.hackathon.wsd.algorithm.dto;

public record FuelStation(
    String id,
    String nom,
    double latitude,
    double longitude,
    double prixParLitre  // €/L
) {}
