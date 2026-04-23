package esgi.hackathon.wsd.algorithm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import esgi.hackathon.wsd.algorithm.dto.GenerationResultDto;
import esgi.hackathon.wsd.algorithm.dto.VroomStep;
import esgi.hackathon.wsd.algorithm.dto.VroomVehicleRoute;
import esgi.hackathon.wsd.algorithm.dto.Waypoint;
import esgi.hackathon.wsd.entity.logistics.Truck;
import esgi.hackathon.wsd.entity.operations.Itinerary;
import esgi.hackathon.wsd.entity.operations.Order;
import esgi.hackathon.wsd.entity.operations.Trip;
import esgi.hackathon.wsd.enums.FuelType;
import esgi.hackathon.wsd.enums.OrderStatus;
import esgi.hackathon.wsd.enums.TripStatus;
import esgi.hackathon.wsd.enums.TruckStatus;
import esgi.hackathon.wsd.repository.ItineraryRepository;
import esgi.hackathon.wsd.repository.OrderRepository;
import esgi.hackathon.wsd.repository.TripRepository;
import esgi.hackathon.wsd.repository.TruckRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orchestre la génération complète des itinéraires pour une date donnée.
 *
 * Phase 1 : Géocodage des commandes sans coordonnées.
 * Phase 2 : Optimisation VROOM (répartition commandes ↔ camions).
 * Phase 3 : Insertion des arrêts carburant segment par segment.
 * Phase 4 : Persistance — Trip + Itinerary + mise à jour statuts commandes.
 */
@Service
public class ItineraryGenerationService {

    private static final Logger log = LoggerFactory.getLogger(ItineraryGenerationService.class);
    private static final double ROAD_FACTOR = 1.3;

    @Value("${algorithm.depot.latitude}")
    private double depotLat;

    @Value("${algorithm.depot.longitude}")
    private double depotLng;

    private final VroomService vroomService;
    private final FuelPlanningService fuelPlanningService;
    private final GeocodingService geocodingService;
    private final OrderRepository orderRepository;
    private final TruckRepository truckRepository;
    private final TripRepository tripRepository;
    private final ItineraryRepository itineraryRepository;
    private final ObjectMapper objectMapper;

    public ItineraryGenerationService(
        VroomService vroomService,
        FuelPlanningService fuelPlanningService,
        GeocodingService geocodingService,
        OrderRepository orderRepository,
        TruckRepository truckRepository,
        TripRepository tripRepository,
        ItineraryRepository itineraryRepository,
        ObjectMapper objectMapper
    ) {
        this.vroomService         = vroomService;
        this.fuelPlanningService  = fuelPlanningService;
        this.geocodingService     = geocodingService;
        this.orderRepository      = orderRepository;
        this.truckRepository      = truckRepository;
        this.tripRepository       = tripRepository;
        this.itineraryRepository  = itineraryRepository;
        this.objectMapper         = objectMapper;
    }

    @Transactional
    public GenerationResultDto generateForDate(LocalDate date) {
        // Phase 1 — récupérer et géocoder les commandes en attente
        List<Order> orders = orderRepository.findByStatusAndRequestedDate(OrderStatus.PENDING, date);
        log.info("Génération itinéraires pour {} — {} commande(s) en attente", date, orders.size());

        for (Order order : orders) {
            if (order.getLatitude() == null && order.getAddressText() != null) {
                double[] coords = geocodingService.geocode(order.getAddressText());
                if (coords != null) {
                    order.setLatitude(coords[0]);
                    order.setLongitude(coords[1]);
                    orderRepository.save(order);
                }
            }
        }

        orders = orders.stream().filter(o -> o.getLatitude() != null).toList();
        if (orders.isEmpty()) {
            log.warn("Aucune commande géocodable pour le {}", date);
            return new GenerationResultDto(0, List.of(), 0);
        }

        // Phase 2 — optimisation VROOM
        List<Truck> trucks = truckRepository.findByStatus(TruckStatus.AVAILABLE);
        if (trucks.isEmpty()) {
            log.warn("Aucun camion disponible pour le {}", date);
            return new GenerationResultDto(0, List.of(), 0);
        }

        List<VroomVehicleRoute> routes = vroomService.optimize(trucks, orders, date);
        if (routes.isEmpty()) {
            log.warn("VROOM n'a retourné aucune route pour le {}", date);
            return new GenerationResultDto(0, List.of(), 0);
        }

        Map<Long, Order> orderMap = orders.stream().collect(Collectors.toMap(Order::getId, o -> o));
        Map<Long, Truck> truckMap = trucks.stream().collect(Collectors.toMap(Truck::getId, t -> t));

        List<Long> createdTripIds = new ArrayList<>();
        int totalAssigned = 0;

        // Phase 3 & 4 — pour chaque tournée retournée par VROOM
        for (VroomVehicleRoute route : routes) {
            Truck truck = truckMap.get(route.vehicleId());
            if (truck == null) continue;

            // Construire la liste de waypoints à partir des steps VROOM
            List<Waypoint> waypoints = new ArrayList<>();
            waypoints.add(Waypoint.depot(depotLat, depotLng, LocalDateTime.of(date, LocalTime.of(7, 0))));

            List<Order> routeOrders = new ArrayList<>();
            for (VroomStep step : route.steps()) {
                if (!"job".equals(step.type()) || step.jobId() == null) continue;
                Order order = orderMap.get(step.jobId());
                if (order == null) continue;

                LocalDateTime eta = step.arrivalUnix() > 0
                    ? LocalDateTime.ofEpochSecond(step.arrivalUnix(), 0, ZoneOffset.UTC)
                    : null;
                waypoints.add(Waypoint.livraison(order.getLatitude(), order.getLongitude(), order.getId(), eta));
                routeOrders.add(order);
            }

            waypoints.add(Waypoint.depot(depotLat, depotLng, null));

            // Distances haversine × road factor par segment (approx. route)
            List<Double> segDists = new ArrayList<>();
            for (int i = 0; i < waypoints.size() - 1; i++) {
                Waypoint a = waypoints.get(i);
                Waypoint b = waypoints.get(i + 1);
                segDists.add(RoutingService.haversine(a.latitude(), a.longitude(), b.latitude(), b.longitude()) * ROAD_FACTOR);
            }

            // Phase 3 — insérer les arrêts carburant
            FuelType fuelType = truck.getModel() != null && truck.getModel().getFuelType() != null
                ? truck.getModel().getFuelType() : FuelType.DIESEL;
            List<Waypoint> finalWaypoints = fuelPlanningService.insertFuelStops(waypoints, segDists, truck, fuelType);

            // Phase 4 — persister Trip
            Trip trip = new Trip();
            trip.setTruck(truck);
            trip.setDate(date);
            trip.setStatus(TripStatus.PLANNED);
            routeOrders.stream()
                .map(Order::getTimeSlot)
                .filter(Objects::nonNull)
                .findFirst()
                .ifPresent(trip::setTimeSlot);
            trip = tripRepository.save(trip);

            // Persister Itinerary avec le GPS en JSON
            double distKm = route.distanceMetres() / 1000.0;
            double durationMin = route.durationSeconds() / 60.0;
            Itinerary itinerary = new Itinerary();
            itinerary.setTrip(trip);
            itinerary.setDuration(durationMin);
            try {
                String gpsJson = objectMapper.writeValueAsString(finalWaypoints);
                itinerary.setGpsData(gpsJson);
            } catch (Exception e) {
                log.error("Erreur sérialisation gpsData: {}", e.getMessage());
            }
            itineraryRepository.save(itinerary);

            // Mettre à jour les commandes : ASSIGNED + lien trip
            Trip savedTrip = trip;
            for (Order order : routeOrders) {
                order.setStatus(OrderStatus.ASSIGNED);
                order.setTrip(savedTrip);
                orderRepository.save(order);
            }

            log.info("Tournée créée : tripId={}, camion={}, {} livraison(s), {} km",
                trip.getId(), truck.getLicensePlate(), routeOrders.size(), Math.round(distKm));

            createdTripIds.add(trip.getId());
            totalAssigned += routeOrders.size();
        }

        return new GenerationResultDto(createdTripIds.size(), createdTripIds, totalAssigned);
    }
}
