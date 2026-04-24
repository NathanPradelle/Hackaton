package esgi.hackathon.wsd.service;

import esgi.hackathon.wsd.dto.TripDto;
import esgi.hackathon.wsd.entity.logistics.Truck;
import esgi.hackathon.wsd.entity.operations.Order;
import esgi.hackathon.wsd.entity.users.Driver;
import esgi.hackathon.wsd.enums.DriverStatus;
import esgi.hackathon.wsd.enums.OrderStatus;
import esgi.hackathon.wsd.enums.TruckStatus;
import esgi.hackathon.wsd.mapper.TripMapper;
import esgi.hackathon.wsd.entity.operations.Trip;
import esgi.hackathon.wsd.enums.TripStatus;
import esgi.hackathon.wsd.repository.OrderRepository;
import esgi.hackathon.wsd.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripService {

  private final TripRepository tripRepository;
  private final TripMapper tripMapper;
  private final OrderRepository orderRepository;

  @Transactional(readOnly = true)
  public List<TripDto> getAllTrips() {
    return tripRepository.findAll().stream().map(tripMapper::toDto).toList();
  }

  @Transactional
  public TripDto createTrip(TripDto tripDto) {
    Trip trip = tripMapper.toEntity(tripDto);

    if (trip.getDriver() != null && trip.getDriver().getId() == null) {
      trip.setDriver(null);
    }
    if (trip.getTruck() != null && trip.getTruck().getId() == null) {
      trip.setTruck(null);
    }

    trip.setStatus(TripStatus.PLANNED);
    return tripMapper.toDto(tripRepository.save(trip));
  }
  @Transactional
  public TripDto updateTripStatus(Long id, TripStatus newStatus) {
    Trip trip = tripRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Trip introuvable"));

    // On déclenche les changements en cascade selon la transition
    switch (newStatus) {
      case IN_PROGRESS -> startTrip(trip);
      case COMPLETED -> finishTrip(trip);
      case CANCELLED -> cancelTrip(trip);
      default -> trip.setStatus(newStatus);
    }

    return tripMapper.toDto(tripRepository.save(trip));
  }

  private void startTrip(Trip trip) {
    trip.setStatus(TripStatus.IN_PROGRESS);

    // Le chauffeur et le camion deviennent indisponibles
    if (trip.getDriver() != null) trip.getDriver().setStatus(DriverStatus.ON_TRIP);
    if (trip.getTruck() != null) trip.getTruck().setStatus(TruckStatus.BUSY);

    // Toutes les commandes liées passent en "En cours de livraison"
    List<Order> orders = orderRepository.findByTripId(trip.getId());
    orders.forEach(o -> o.setStatus(OrderStatus.PICKED_UP));
    orderRepository.saveAll(orders);
  }

  private void finishTrip(Trip trip) {
    trip.setStatus(TripStatus.COMPLETED);

    // On libère les ressources
    if (trip.getDriver() != null) trip.getDriver().setStatus(DriverStatus.AVAILABLE);
    if (trip.getTruck() != null) trip.getTruck().setStatus(TruckStatus.AVAILABLE);

    // On valide les commandes
    List<Order> orders = orderRepository.findByTripId(trip.getId());
    orders.forEach(o -> o.setStatus(OrderStatus.DELIVERED));
    orderRepository.saveAll(orders);
  }

  private void cancelTrip(Trip trip) {
    trip.setStatus(TripStatus.CANCELLED);

    // On libère les ressources immédiatement
    if (trip.getDriver() != null) trip.getDriver().setStatus(DriverStatus.AVAILABLE);
    if (trip.getTruck() != null) trip.getTruck().setStatus(TruckStatus.AVAILABLE);

    // TRÈS IMPORTANT : Les commandes redeviennent PENDING pour être reprises par l'algo
    List<Order> orders = orderRepository.findByTripId(trip.getId());
    orders.forEach(o -> {
      o.setStatus(OrderStatus.PENDING);
      o.setTrip(null); // On casse le lien
    });
    orderRepository.saveAll(orders);
  }
}
