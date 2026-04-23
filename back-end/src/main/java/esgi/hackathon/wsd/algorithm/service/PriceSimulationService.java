package esgi.hackathon.wsd.algorithm.service;

import esgi.hackathon.wsd.entity.logistics.Truck;
import esgi.hackathon.wsd.entity.operations.Order;
import esgi.hackathon.wsd.enums.FuelType;
import esgi.hackathon.wsd.enums.OrderStatus;
import esgi.hackathon.wsd.enums.TruckStatus;
import esgi.hackathon.wsd.repository.OrderRepository;
import esgi.hackathon.wsd.repository.TruckRepository;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Simulation légère déclenchée à la création d'une commande.
 * Estime le prix proratisé en fonction du trajet groupé et applique le coefficient de marge.
 */
@Service
public class PriceSimulationService {

    private static final Logger log = LoggerFactory.getLogger(PriceSimulationService.class);
    private static final double ROAD_FACTOR = 1.3;

    @Value("${algorithm.depot.latitude}")
    private double depotLat;

    @Value("${algorithm.depot.longitude}")
    private double depotLng;

    @Value("${algorithm.price.margin-coefficient}")
    private double marginCoefficient;

    private final GeocodingService geocodingService;
    private final OrderGroupingService orderGroupingService;
    private final FuelPlanningService fuelPlanningService;
    private final OrderRepository orderRepository;
    private final TruckRepository truckRepository;

    public PriceSimulationService(
        GeocodingService geocodingService,
        OrderGroupingService orderGroupingService,
        FuelPlanningService fuelPlanningService,
        OrderRepository orderRepository,
        TruckRepository truckRepository
    ) {
        this.geocodingService     = geocodingService;
        this.orderGroupingService = orderGroupingService;
        this.fuelPlanningService  = fuelPlanningService;
        this.orderRepository      = orderRepository;
        this.truckRepository      = truckRepository;
    }

    /**
     * Calcule le prix estimé pour une commande.
     * Géocode l'adresse si les coordonnées sont absentes (et les pose sur l'entité).
     * Retourne 0.0 si le calcul n'est pas possible.
     */
    public double simulatePrice(Order newOrder) {
        if (newOrder.getLatitude() == null && newOrder.getAddressText() != null) {
            double[] coords = geocodingService.geocode(newOrder.getAddressText());
            if (coords != null) {
                newOrder.setLatitude(coords[0]);
                newOrder.setLongitude(coords[1]);
            }
        }

        if (newOrder.getLatitude() == null) {
            log.warn("Impossible de simuler le prix : coordonnées inconnues pour '{}'", newOrder.getAddressText());
            return 0.0;
        }

        // Paramètres du premier camion disponible (fallback sur des valeurs par défaut)
        List<Truck> available = truckRepository.findByStatus(TruckStatus.AVAILABLE);
        Truck refTruck = available.stream().filter(t -> t.getModel() != null).findFirst().orElse(null);

        int truckCapacity = refTruck != null && refTruck.getModel().getCapacity() != null
            ? refTruck.getModel().getCapacity().intValue() : 100;
        double consumption = refTruck != null && refTruck.getModel().getFuelConsumption() != null
            ? refTruck.getModel().getFuelConsumption() : 30.0;
        FuelType fuelType = refTruck != null && refTruck.getModel().getFuelType() != null
            ? refTruck.getModel().getFuelType() : FuelType.DIESEL;

        // Commandes existantes compatibles pour le même jour
        List<Order> candidates = newOrder.getRequestedDate() != null
            ? orderRepository.findByStatusAndRequestedDate(OrderStatus.PENDING, newOrder.getRequestedDate())
            : List.of();
        List<Order> grouped = orderGroupingService.findCompatible(newOrder, candidates, truckCapacity);

        List<Order> allStops = new ArrayList<>();
        allStops.add(newOrder);
        allStops.addAll(grouped);

        double totalDistKm = estimateRouteDistance(allStops);
        double pricePerLitre = FuelPlanningService.defaultFuelPrice(fuelType);
        double fuelCost = fuelPlanningService.estimateFuelCost(totalDistKm, consumption, pricePerLitre);

        int totalQty = allStops.stream().mapToInt(o -> o.getQuantity() != null ? o.getQuantity() : 1).sum();
        int orderQty = newOrder.getQuantity() != null ? newOrder.getQuantity() : 1;
        double prorated = totalQty > 0 ? fuelCost * ((double) orderQty / totalQty) : fuelCost;

        double price = Math.round(prorated * marginCoefficient * 100.0) / 100.0;
        log.info("Prix simulé : {} carton(s), {} km estimés → {} €", orderQty, Math.round(totalDistKm), price);
        return price;
    }

    /** Nearest-neighbor depuis le dépôt pour estimer la distance route de la tournée. */
    private double estimateRouteDistance(List<Order> orders) {
        List<Order> remaining = new ArrayList<>(orders.stream()
            .filter(o -> o.getLatitude() != null)
            .toList());

        double totalDist = 0;
        double curLat = depotLat;
        double curLng = depotLng;

        while (!remaining.isEmpty()) {
            final double lat = curLat;
            final double lng = curLng;
            Order nearest = remaining.stream()
                .min((a, b) -> Double.compare(
                    RoutingService.haversine(lat, lng, a.getLatitude(), a.getLongitude()),
                    RoutingService.haversine(lat, lng, b.getLatitude(), b.getLongitude())
                ))
                .orElseThrow();

            totalDist += RoutingService.haversine(curLat, curLng, nearest.getLatitude(), nearest.getLongitude()) * ROAD_FACTOR;
            curLat = nearest.getLatitude();
            curLng = nearest.getLongitude();
            remaining.remove(nearest);
        }

        totalDist += RoutingService.haversine(curLat, curLng, depotLat, depotLng) * ROAD_FACTOR;
        return totalDist;
    }
}
