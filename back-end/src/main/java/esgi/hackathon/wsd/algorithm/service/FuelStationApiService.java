package esgi.hackathon.wsd.algorithm.service;

import esgi.hackathon.wsd.algorithm.dto.FuelStation;
import esgi.hackathon.wsd.enums.FuelType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class FuelStationApiService {

    private static final Logger log = LoggerFactory.getLogger(FuelStationApiService.class);

    // Rayon approximatif en degrés pour la bounding box (~5 km)
    private static final double RADIUS_DEG = 0.045;

    private final RestClient restClient;

    public FuelStationApiService() {
        this.restClient = RestClient.builder()
            .baseUrl("https://data.economie.gouv.fr/api/explore/v2.1/catalog/datasets")
            .build();
    }

    /**
     * Retourne les stations-service proches d'un point, triées par détour haversine.
     * Les prix sont en €/L.
     */
    @SuppressWarnings("unchecked")
    public List<FuelStation> findNearby(double lat, double lng, FuelType fuelType) {
        String priceField = priceFieldFor(fuelType);
        if (priceField == null) return List.of(); // camion électrique : pas de station essence

        // Bounding box simple pour éviter une requête géospatiale complexe
        String where = String.format(
            "latitude > %f AND latitude < %f AND longitude > %f AND longitude < %f AND %s IS NOT NULL",
            lat - RADIUS_DEG, lat + RADIUS_DEG,
            lng - RADIUS_DEG, lng + RADIUS_DEG,
            priceField
        );

        try {
            Map<String, Object> response = restClient.get()
                .uri("/prix-des-carburants-en-france-flux-instantane-v2/records"
                    + "?select=id,nom,adresse,latitude,longitude,{priceField}"
                    + "&where={where}&limit=20",
                    priceField, where)
                .retrieve()
                .body(Map.class);

            if (response == null) return List.of();

            List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
            if (results == null) return List.of();

            List<FuelStation> stations = new ArrayList<>();
            for (Map<String, Object> r : results) {
                try {
                    double sLat = ((Number) r.get("latitude")).doubleValue();
                    double sLng = ((Number) r.get("longitude")).doubleValue();
                    double prix = ((Number) r.get(priceField)).doubleValue();
                    // L'API renvoie les prix en centimes si > 10, sinon en euros
                    if (prix > 10) prix = prix / 1000.0;

                    stations.add(new FuelStation(
                        String.valueOf(r.get("id")),
                        String.valueOf(r.getOrDefault("nom", r.getOrDefault("adresse", "Station"))),
                        sLat, sLng, prix
                    ));
                } catch (Exception ignored) {
                    // Station avec données manquantes : ignorée
                }
            }

            // Trier par distance haversine croissante
            stations.sort((a, b) -> Double.compare(
                RoutingService.haversine(lat, lng, a.latitude(), a.longitude()),
                RoutingService.haversine(lat, lng, b.latitude(), b.longitude())
            ));

            return stations;

        } catch (Exception e) {
            log.error("Erreur API prix-carburant: {}", e.getMessage());
            return List.of();
        }
    }

    private String priceFieldFor(FuelType fuelType) {
        return switch (fuelType) {
            case DIESEL   -> "prix_gazole";
            case GASOLINE -> "prix_sp95_e10";
            case GNV      -> "prix_gplc";
            case ELECTRIC -> null;
        };
    }
}
