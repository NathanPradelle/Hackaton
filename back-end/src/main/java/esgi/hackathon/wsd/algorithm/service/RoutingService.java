package esgi.hackathon.wsd.algorithm.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class RoutingService {

    private static final Logger log = LoggerFactory.getLogger(RoutingService.class);

    private final RestClient restClient;

    @Value("${algorithm.ors.api-key}")
    private String apiKey;

    public record RouteResult(double distanceKm, int durationMinutes, List<Double> segmentDistancesKm) {}

    public RoutingService(@Value("${algorithm.ors.base-url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    /**
     * Calcule le trajet routier camion (HGV) entre une séquence de points.
     * coordinates : liste de [latitude, longitude]
     * ORS attend [longitude, latitude] — la conversion est faite ici.
     */
    @SuppressWarnings("unchecked")
    public RouteResult getRoute(List<double[]> coordinates) {
        if (coordinates == null || coordinates.size() < 2) {
            return new RouteResult(0, 0, List.of());
        }

        // ORS attend [[lng, lat], [lng, lat], ...]
        List<List<Double>> orsCoords = coordinates.stream()
            .map(c -> List.of(c[1], c[0]))
            .toList();

        Map<String, Object> body = Map.of(
            "coordinates", orsCoords,
            "instructions", false
        );

        try {
            Map<String, Object> response = restClient.post()
                .uri("/v2/directions/driving-hgv")
                .header("Authorization", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);

            if (response == null) return new RouteResult(0, 0, List.of());

            List<Map<String, Object>> routes = (List<Map<String, Object>>) response.get("routes");
            Map<String, Object> summary = (Map<String, Object>) routes.get(0).get("summary");

            double distanceM = ((Number) summary.get("distance")).doubleValue();
            double durationS = ((Number) summary.get("duration")).doubleValue();

            // Distances par segment
            List<Map<String, Object>> segments = (List<Map<String, Object>>) routes.get(0).get("segments");
            List<Double> segDists = new ArrayList<>();
            if (segments != null) {
                for (Map<String, Object> seg : segments) {
                    segDists.add(((Number) seg.get("distance")).doubleValue() / 1000.0);
                }
            }

            return new RouteResult(distanceM / 1000.0, (int) (durationS / 60.0), segDists);

        } catch (Exception e) {
            log.error("Erreur routing ORS: {}", e.getMessage());
            return new RouteResult(0, 0, List.of());
        }
    }

    /**
     * Calcule la distance à vol d'oiseau entre deux points (formule haversine) en km.
     * Utilisé pour les estimations rapides sans appel API.
     */
    public static double haversine(double lat1, double lng1, double lat2, double lng2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
