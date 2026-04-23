package esgi.hackathon.wsd.algorithm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Détail du calcul de prix retourné au client avant confirmation de commande.
 * Tous les montants sont en euros, arrondis à 2 décimales.
 */
public record PriceBreakdownDto(
    @JsonProperty("distanceKm")      double distanceKm,
    @JsonProperty("coutCarburant")   double coutCarburant,
    @JsonProperty("partSalariale")   double partSalariale,
    @JsonProperty("usureCamion")     double usureCamion,
    @JsonProperty("badgeTelepeage")  double badgeTelepeage,
    @JsonProperty("fraisLivraison")  double fraisLivraison,
    @JsonProperty("sousTotal")       double sousTotal,
    @JsonProperty("marge")           double marge,
    @JsonProperty("prixTotal")       double prixTotal
) {}
