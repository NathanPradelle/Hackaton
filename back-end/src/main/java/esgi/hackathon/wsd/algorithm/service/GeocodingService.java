package esgi.hackathon.wsd.algorithm.service;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GeocodingService {

    private static final Logger log = LoggerFactory.getLogger(GeocodingService.class);

    private final RestClient restClient;

    @Value("${algorithm.ors.api-key}")
    private String apiKey;

    public GeocodingService(@Value("${algorithm.ors.base-url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    /**
     * Convertit une adresse textuelle en coordonnées GPS [latitude, longitude].
     * Retourne null si l'adresse ne peut pas être géocodée.
     */
    @SuppressWarnings("unchecked")
    public double[] geocode(String address) {
        try {
            Map<String, Object> response = restClient.get()
                .uri("/geocode/search?api_key={key}&text={text}&boundary.country=FR&size=1",
                    apiKey, address)
                .retrieve()
                .body(Map.class);

            if (response == null) return null;

            List<Map<String, Object>> features = (List<Map<String, Object>>) response.get("features");
            if (features == null || features.isEmpty()) return null;

            Map<String, Object> geometry = (Map<String, Object>) features.get(0).get("geometry");
            List<Double> coords = (List<Double>) geometry.get("coordinates"); // [lng, lat]

            return new double[]{coords.get(1), coords.get(0)}; // retourne [lat, lng]

        } catch (Exception e) {
            log.error("Erreur géocodage pour '{}': {}", address, e.getMessage());
            return null;
        }
    }
}
