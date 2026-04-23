package esgi.hackathon.wsd.algorithm.service;

import esgi.hackathon.wsd.algorithm.dto.FuelStation;
import esgi.hackathon.wsd.algorithm.dto.Waypoint;
import esgi.hackathon.wsd.entity.logistics.Truck;
import esgi.hackathon.wsd.enums.FuelType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Insère les arrêts carburant dans un itinéraire en fonction du niveau de réservoir.
 *
 * Hypothèses simplifiées :
 *  - On fait le plein complet à chaque arrêt.
 *  - Le détour est estimé en haversine × 1.3 (facteur route).
 *  - L'ETA après un arrêt carburant est décalé de 15 min (temps de remplissage).
 */
@Service
public class FuelPlanningService {

    private static final Logger log = LoggerFactory.getLogger(FuelPlanningService.class);
    private static final double ROAD_FACTOR = 1.3;   // distance haversine → distance route
    private static final int REFUEL_TIME_MIN = 15;

    @Value("${algorithm.fuel.safety-threshold-percent}")
    private int safetyThresholdPercent;

    private final FuelStationApiService fuelStationApi;

    public FuelPlanningService(FuelStationApiService fuelStationApi) {
        this.fuelStationApi = fuelStationApi;
    }

    /**
     * Prend la liste ordonnée de waypoints (sans arrêts carburant) et les
     * distances de chaque segment, et insère les arrêts nécessaires.
     *
     * @param waypoints     séquence depot → livraisons → depot
     * @param segmentDistKm distance de chaque segment i→i+1 (taille = waypoints.size()-1)
     * @param truck         camion (pour consommation, type carburant, réservoir)
     * @param fuelType      type de carburant
     * @return nouvelle liste de waypoints avec arrêts carburant insérés
     */
    public List<Waypoint> insertFuelStops(
        List<Waypoint> waypoints,
        List<Double> segmentDistKm,
        Truck truck,
        FuelType fuelType
    ) {
        if (truck.getModel() == null) return waypoints;

        double tankCapacity   = truck.getModel().getTankCapacity();
        double consumption    = truck.getModel().getFuelConsumption(); // L/100km
        double currentFuel    = truck.getCurrentFuelLevel() != null ? truck.getCurrentFuelLevel() : tankCapacity;
        double safetyThreshold = tankCapacity * safetyThresholdPercent / 100.0;

        List<Waypoint> result = new ArrayList<>();
        result.add(waypoints.get(0)); // dépôt départ

        for (int i = 0; i < waypoints.size() - 1; i++) {
            double segDist = i < segmentDistKm.size() ? segmentDistKm.get(i) : 0;
            double consumed = (segDist / 100.0) * consumption;

            // Vérifier si on passe sous le seuil de sécurité sur ce segment
            if (currentFuel - consumed < safetyThreshold) {
                Waypoint from = waypoints.get(i);
                FuelStation best = findBestStation(from.latitude(), from.longitude(), fuelType, consumed);

                if (best != null) {
                    double detourKm = RoutingService.haversine(
                        from.latitude(), from.longitude(),
                        best.latitude(), best.longitude()
                    ) * 2 * ROAD_FACTOR; // aller-retour

                    // ETA station = ETA waypoint précédent + temps estimé
                    LocalDateTime stationEta = from.eta() != null
                        ? from.eta().plusMinutes(Math.round(detourKm / 60.0 * 60) + REFUEL_TIME_MIN)
                        : null;

                    result.add(Waypoint.carburant(best.latitude(), best.longitude(), best.nom(), stationEta));
                    log.info("Arrêt carburant inséré : {} (détour ~{} km)", best.nom(), Math.round(detourKm));

                    currentFuel = tankCapacity; // plein complet
                    consumed += (detourKm / 100.0) * consumption; // carburant du détour
                } else {
                    log.warn("Aucune station trouvée près de [{}, {}]", from.latitude(), from.longitude());
                }
            }

            currentFuel -= consumed;
            result.add(waypoints.get(i + 1));
        }

        return result;
    }

    private FuelStation findBestStation(double lat, double lng, FuelType fuelType, double neededLitres) {
        List<FuelStation> stations = fuelStationApi.findNearby(lat, lng, fuelType);
        if (stations.isEmpty()) return null;

        // Choisir la station qui minimise : distance_détour × consommation × prix
        // Pour la simulation on prend simplement la plus proche avec un prix raisonnable.
        return stations.stream()
            .min((a, b) -> {
                double distA = RoutingService.haversine(lat, lng, a.latitude(), a.longitude());
                double distB = RoutingService.haversine(lat, lng, b.latitude(), b.longitude());
                // Score = détour (km) × prix/L
                double scoreA = distA * a.prixParLitre();
                double scoreB = distB * b.prixParLitre();
                return Double.compare(scoreA, scoreB);
            })
            .orElse(null);
    }

    /**
     * Estime le coût carburant total d'une route (sans arrêts, pour la simulation rapide).
     */
    public double estimateFuelCost(double distanceKm, double consumptionL100, double pricePerLitre) {
        return (distanceKm / 100.0) * consumptionL100 * pricePerLitre;
    }

    /** Prix moyen de l'essence en France (fallback si l'API est indisponible). */
    public static double defaultFuelPrice(FuelType fuelType) {
        return switch (fuelType) {
            case DIESEL   -> 1.75;
            case GASOLINE -> 1.80;
            case GNV      -> 1.10;
            case ELECTRIC -> 0.0;
        };
    }
}
