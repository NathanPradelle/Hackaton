package esgi.hackathon.wsd.algorithm.service;

import esgi.hackathon.wsd.entity.operations.Order;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Identifie les commandes existantes compatibles pour être groupées
 * avec une nouvelle commande (même camionnée).
 *
 * Critères :
 *  - Distance haversine entre adresses ≤ maxDistanceKm
 *  - Plages horaires compatibles (chevauchement ou écart ≤ maxTimeGapMinutes)
 *  - Quantité totale restant dans la capacité du camion
 *
 * Format timeSlot attendu : "HH:mm-HH:mm"  (ex. "10:00-12:00")
 */
@Service
public class OrderGroupingService {

    @Value("${algorithm.grouping.max-distance-km}")
    private double maxDistanceKm;

    @Value("${algorithm.grouping.max-time-gap-minutes}")
    private int maxTimeGapMinutes;

    /**
     * Retourne les commandes de candidates compatibles avec newOrder.
     * truckCapacity : capacité totale du camion (en cartons).
     */
    public List<Order> findCompatible(Order newOrder, List<Order> candidates, int truckCapacity) {
        if (newOrder.getLatitude() == null || newOrder.getLongitude() == null) return List.of();

        List<Order> compatible = new ArrayList<>();
        int usedCapacity = newOrder.getQuantity() != null ? newOrder.getQuantity() : 0;

        for (Order candidate : candidates) {
            if (candidate.getId().equals(newOrder.getId())) continue;
            if (candidate.getLatitude() == null || candidate.getLongitude() == null) continue;

            double dist = RoutingService.haversine(
                newOrder.getLatitude(), newOrder.getLongitude(),
                candidate.getLatitude(), candidate.getLongitude()
            );
            if (dist > maxDistanceKm) continue;

            if (!timeSlotsCompatible(newOrder.getTimeSlot(), candidate.getTimeSlot())) continue;

            int candidateQty = candidate.getQuantity() != null ? candidate.getQuantity() : 0;
            if (usedCapacity + candidateQty > truckCapacity) continue;

            compatible.add(candidate);
            usedCapacity += candidateQty;
        }

        return compatible;
    }

    /**
     * Vérifie que deux plages "HH:mm-HH:mm" se chevauchent ou sont à moins de maxTimeGapMinutes.
     * Retourne true si les slots sont incompatibles ou vides (on laisse passer en cas de données manquantes).
     */
    private boolean timeSlotsCompatible(String slot1, String slot2) {
        if (slot1 == null || slot2 == null) return true;
        try {
            int[] s1 = parseSlot(slot1);
            int[] s2 = parseSlot(slot2);
            // Chevauchement
            if (s1[0] <= s2[1] && s2[0] <= s1[1]) return true;
            // Écart entre les deux fenêtres
            int gap = Math.max(s2[0] - s1[1], s1[0] - s2[1]);
            return gap <= maxTimeGapMinutes;
        } catch (DateTimeParseException e) {
            return true; // format inconnu : on ne bloque pas
        }
    }

    /** Retourne [startMinutes, endMinutes] depuis "HH:mm-HH:mm". */
    private int[] parseSlot(String slot) {
        String[] parts = slot.split("-");
        LocalTime start = LocalTime.parse(parts[0].trim());
        LocalTime end = LocalTime.parse(parts[1].trim());
        return new int[]{start.toSecondOfDay() / 60, end.toSecondOfDay() / 60};
    }
}
