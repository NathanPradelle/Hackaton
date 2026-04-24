package esgi.hackathon.wsd.algorithm.service;

import esgi.hackathon.wsd.algorithm.dto.PriceBreakdownDto;
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
 * Simulation de prix déclenchée à la demande du client (avant confirmation)
 * et automatiquement lors de la création de commande.
 *
 * Formule :
 * coût_km = carburant_km + salarial_km + usure_km + péage_km
 * coût_tournée = coût_km × distance + frais_stop × nb_stops
 * part_commande = coût_tournée × (qté_commande / MAX(qté_totale, seuil_rentabilité))
 * prix = part_commande × (1 + marge)
 */
@Service
public class PriceSimulationService {

    private static final Logger log = LoggerFactory.getLogger(PriceSimulationService.class);
    private static final double ROAD_FACTOR = 1.3;

    @Value("${algorithm.depot.latitude}")  private double depotLat;
    @Value("${algorithm.depot.longitude}") private double depotLng;

    @Value("${algorithm.price.driver-cost-per-km}") private double driverCostPerKm;
    @Value("${algorithm.price.wear-cost-per-km}")   private double wearCostPerKm;
    @Value("${algorithm.price.toll-cost-per-km}")   private double tollCostPerKm;
    @Value("${algorithm.price.stop-fee}")           private double stopFee;
    @Value("${algorithm.price.margin-rate}")        private double marginRate;

    private final GeocodingService geocodingService;
    private final OrderGroupingService orderGroupingService;
    private final OrderRepository orderRepository;
    private final TruckRepository truckRepository;

    public PriceSimulationService(
        GeocodingService geocodingService,
        OrderGroupingService orderGroupingService,
        OrderRepository orderRepository,
        TruckRepository truckRepository
    ) {
        this.geocodingService     = geocodingService;
        this.orderGroupingService = orderGroupingService;
        this.orderRepository      = orderRepository;
        this.truckRepository      = truckRepository;
    }

    /**
     * Calcule le prix estimé et retourne le détail du calcul.
     * Géocode l'adresse si les coordonnées sont absentes (les pose sur l'entité).
     */
    public PriceBreakdownDto simulateBreakdown(Order newOrder) {
        if (newOrder.getLatitude() == null && newOrder.getAddressText() != null) {
            double[] coords = geocodingService.geocode(newOrder.getAddressText());
            if (coords != null) {
                newOrder.setLatitude(coords[0]);
                newOrder.setLongitude(coords[1]);
            }
        }

        if (newOrder.getLatitude() == null) {
            log.warn("Coordonnées introuvables pour '{}' — prix à 0", newOrder.getAddressText());
            return zero();
        }

        // Référence : premier camion diesel disponible (fallback si aucun)
        List<Truck> available = truckRepository.findByStatus(TruckStatus.AVAILABLE);
        Truck ref = available.stream()
            .filter(t -> t.getModel() != null && t.getModel().getFuelConsumption() != null
                && t.getModel().getFuelConsumption() > 0)
            .findFirst().orElse(null);

        int truckCapacity = ref != null && ref.getModel().getCapacity() != null
            ? ref.getModel().getCapacity().intValue() : 100;
        double consumption = ref != null ? ref.getModel().getFuelConsumption() : 8.5; // L/100 km
        FuelType fuelType  = ref != null && ref.getModel().getFuelType() != null
            ? ref.getModel().getFuelType() : FuelType.DIESEL;

        // Commandes existantes compatibles pour groupage
        List<Order> candidates = newOrder.getRequestedDate() != null
            ? orderRepository.findByStatusAndRequestedDate(OrderStatus.PENDING, newOrder.getRequestedDate())
            : List.of();
        List<Order> grouped = orderGroupingService.findCompatible(newOrder, candidates, truckCapacity);

        List<Order> allStops = new ArrayList<>();
        allStops.add(newOrder);
        allStops.addAll(grouped);

        // Calcul exact de la distance de la portion de tournée (sans x2)
        double totalDistKm = estimateRouteDistance(allStops);
        int    totalQty    = allStops.stream().mapToInt(o -> o.getQuantity() != null ? o.getQuantity() : 1).sum();
        int    orderQty    = newOrder.getQuantity() != null ? newOrder.getQuantity() : 1;

        // Le seuil de rentabilité pour l'entreprise (ex: on estime que le camion sera rempli à au moins 70%)
        double expectedMinimumQty = truckCapacity * 0.70; 
        
        // Le ratio protège le client si le camion est quasiment vide
        double ratio = (double) orderQty / Math.max(totalQty, expectedMinimumQty);

        // Coût carburant
        double fuelPricePerLitre = FuelPlanningService.defaultFuelPrice(fuelType);
        double fuelCostPerKm     = (consumption / 100.0) * fuelPricePerLitre;

        // Coût total tournée puis proratisation juste
        double routeFuelCost   = fuelCostPerKm  * totalDistKm * ratio;
        double routeDriverCost = driverCostPerKm * totalDistKm * ratio;
        double routeWearCost   = wearCostPerKm   * totalDistKm * ratio;
        double routeTollCost   = tollCostPerKm   * totalDistKm * ratio;
        double orderStopFee    = stopFee; // frais fixe par livraison, non proratisé

        double subtotal = routeFuelCost + routeDriverCost + routeWearCost + routeTollCost + orderStopFee;
        double marge    = subtotal * marginRate;
        double total    = subtotal + marge;

        log.info("Simulation : {} carton(s), {} km (tournée), ratio={}% → {} €",
            orderQty, Math.round(totalDistKm), Math.round(ratio * 100), round(total));

        return new PriceBreakdownDto(
            round(totalDistKm),
            round(routeFuelCost),
            round(routeDriverCost),
            round(routeWearCost),
            round(routeTollCost),
            round(orderStopFee),
            round(subtotal),
            round(marge),
            round(total)
        );
    }

    /** Raccourci : retourne uniquement le prix total (pour createOrder). */
    public double simulatePrice(Order newOrder) {
        return simulateBreakdown(newOrder).prixTotal();
    }

    private PriceBreakdownDto zero() {
        return new PriceBreakdownDto(0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    /** Nearest-neighbor depuis le dépôt pour estimer la distance route (original conservé) */
    private double estimateRouteDistance(List<Order> orders) {
        List<Order> remaining = new ArrayList<>(orders.stream()
            .filter(o -> o.getLatitude() != null).toList());

        double totalDist = 0;
        double curLat = depotLat;
        double curLng = depotLng;

        while (!remaining.isEmpty()) {
            final double lat = curLat, lng = curLng;
            Order nearest = remaining.stream()
                .min((a, b) -> Double.compare(
                    RoutingService.haversine(lat, lng, a.getLatitude(), a.getLongitude()),
                    RoutingService.haversine(lat, lng, b.getLatitude(), b.getLongitude())))
                .orElseThrow();
            totalDist += RoutingService.haversine(curLat, curLng, nearest.getLatitude(), nearest.getLongitude()) * ROAD_FACTOR;
            curLat = nearest.getLatitude();
            curLng = nearest.getLongitude();
            remaining.remove(nearest);
        }
        return totalDist;
    }
}