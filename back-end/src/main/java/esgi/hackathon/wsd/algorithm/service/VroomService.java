package esgi.hackathon.wsd.algorithm.service;

import esgi.hackathon.wsd.algorithm.dto.VroomStep;
import esgi.hackathon.wsd.algorithm.dto.VroomVehicleRoute;
import esgi.hackathon.wsd.entity.logistics.Truck;
import esgi.hackathon.wsd.entity.operations.Order;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Appelle l'endpoint ORS /optimization (solveur VROOM) pour répartir les
 * commandes entre les camions disponibles en respectant capacités et plages horaires.
 */
@Service
public class VroomService {

    private static final Logger log = LoggerFactory.getLogger(VroomService.class);

    private final RestClient restClient;

    @Value("${algorithm.ors.api-key}")
    private String apiKey;

    @Value("${algorithm.depot.latitude}")
    private double depotLat;

    @Value("${algorithm.depot.longitude}")
    private double depotLng;

    public VroomService(@Value("${algorithm.ors.base-url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    /**
     * Optimise la répartition des commandes entre les camions pour une date donnée.
     * Retourne une route par camion utilisé.
     */
    public List<VroomVehicleRoute> optimize(List<Truck> trucks, List<Order> orders, LocalDate date) {
        List<Map<String, Object>> vehicles = buildVehicles(trucks, date);
        List<Map<String, Object>> jobs = buildJobs(orders, date);

        if (vehicles.isEmpty() || jobs.isEmpty()) return List.of();

        Map<String, Object> body = new HashMap<>();
        body.put("vehicles", vehicles);
        body.put("jobs", jobs);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                .uri("/optimization")
                .header("Authorization", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);

            return parseResponse(response);

        } catch (Exception e) {
            log.error("Erreur VROOM /optimization: {}", e.getMessage());
            return List.of();
        }
    }

    private List<Map<String, Object>> buildVehicles(List<Truck> trucks, LocalDate date) {
        long dayStart = date.atTime(6, 0).toEpochSecond(ZoneOffset.UTC);
        long dayEnd   = date.atTime(22, 0).toEpochSecond(ZoneOffset.UTC);
        List<Double> depot = List.of(depotLng, depotLat); // VROOM: [lng, lat]

        List<Map<String, Object>> vehicles = new ArrayList<>();
        for (Truck truck : trucks) {
            if (truck.getModel() == null) continue;
            int capacity = truck.getModel().getCapacity() != null
                ? truck.getModel().getCapacity().intValue() : 0;

            Map<String, Object> v = new HashMap<>();
            v.put("id", truck.getId().intValue());
            v.put("profile", "driving-hgv");
            v.put("start", depot);
            v.put("end", depot);
            v.put("capacity", List.of(capacity));
            v.put("time_window", List.of(dayStart, dayEnd));
            vehicles.add(v);
        }
        return vehicles;
    }

    private List<Map<String, Object>> buildJobs(List<Order> orders, LocalDate date) {
        List<Map<String, Object>> jobs = new ArrayList<>();
        for (Order order : orders) {
            if (order.getLatitude() == null || order.getLongitude() == null) continue;
            int qty = order.getQuantity() != null ? order.getQuantity() : 1;

            Map<String, Object> job = new HashMap<>();
            job.put("id", order.getId().intValue());
            job.put("location", List.of(order.getLongitude(), order.getLatitude())); // [lng, lat]
            job.put("amount", List.of(qty));
            job.put("service", 300); // 5 min par livraison

            long[] tw = parseTimeWindow(order.getTimeSlot(), date);
            if (tw != null) {
                job.put("time_windows", List.of(List.of(tw[0], tw[1])));
            }
            jobs.add(job);
        }
        return jobs;
    }

    @SuppressWarnings("unchecked")
    private List<VroomVehicleRoute> parseResponse(Map<String, Object> response) {
        if (response == null) return List.of();
        List<Map<String, Object>> routes = (List<Map<String, Object>>) response.get("routes");
        if (routes == null) return List.of();

        List<VroomVehicleRoute> result = new ArrayList<>();
        for (Map<String, Object> route : routes) {
            long vehicleId = ((Number) route.get("vehicle")).longValue();
            log.debug("VROOM route keys: {}", route.keySet());

            // VROOM v1 : distance/duration directement sur la route
            // ORS peut aussi les mettre dans un sous-objet "summary"
            double distanceM = 0;
            long durationS = 0;
            if (route.containsKey("distance")) {
                distanceM = ((Number) route.get("distance")).doubleValue();
            }
            if (route.containsKey("duration")) {
                durationS = ((Number) route.get("duration")).longValue();
            }
            // Fallback : sous-objet summary (certaines versions ORS)
            if (distanceM == 0 || durationS == 0) {
                Map<String, Object> summary = (Map<String, Object>) route.get("summary");
                if (summary != null) {
                    log.debug("VROOM route summary keys: {}", summary.keySet());
                    if (distanceM == 0 && summary.containsKey("distance"))
                        distanceM = ((Number) summary.get("distance")).doubleValue();
                    if (durationS == 0 && summary.containsKey("duration"))
                        durationS = ((Number) summary.get("duration")).longValue();
                }
            }

            List<VroomStep> steps = new ArrayList<>();
            List<Map<String, Object>> rawSteps = (List<Map<String, Object>>) route.get("steps");
            if (rawSteps != null) {
                for (Map<String, Object> s : rawSteps) {
                    String type = (String) s.get("type");
                    Long jobId  = s.containsKey("id") ? ((Number) s.get("id")).longValue() : null;
                    List<Number> loc = (List<Number>) s.get("location"); // [lng, lat]
                    double lat = loc != null ? loc.get(1).doubleValue() : 0;
                    double lng = loc != null ? loc.get(0).doubleValue() : 0;
                    long arrival = s.containsKey("arrival") ? ((Number) s.get("arrival")).longValue() : 0;
                    steps.add(new VroomStep(type, jobId, lat, lng, arrival));
                }
            }

            // Ignorer les routes sans livraisons (seuls start/end)
            boolean hasJobs = steps.stream().anyMatch(s -> "job".equals(s.type()));
            if (hasJobs) {
                result.add(new VroomVehicleRoute(vehicleId, steps, distanceM, durationS));
            }
        }
        return result;
    }

    private long[] parseTimeWindow(String timeSlot, LocalDate date) {
        if (timeSlot == null) return null;
        try {
            String[] parts = timeSlot.split("-");
            LocalTime start = LocalTime.parse(parts[0].trim());
            LocalTime end   = LocalTime.parse(parts[1].trim());
            return new long[]{
                date.atTime(start).toEpochSecond(ZoneOffset.UTC),
                date.atTime(end).toEpochSecond(ZoneOffset.UTC)
            };
        } catch (Exception e) {
            return null;
        }
    }
}
