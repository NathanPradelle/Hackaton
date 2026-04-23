package esgi.hackathon.wsd.service;

import esgi.hackathon.wsd.algorithm.dto.PriceBreakdownDto;
import esgi.hackathon.wsd.algorithm.service.GeocodingService;
import esgi.hackathon.wsd.algorithm.service.PriceSimulationService;
import esgi.hackathon.wsd.dto.OrderDto;
import esgi.hackathon.wsd.entity.operations.Order;
import esgi.hackathon.wsd.enums.OrderStatus;
import esgi.hackathon.wsd.mapper.OrderMapper;
import esgi.hackathon.wsd.repository.OrderRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final OrderMapper orderMapper;
  private final GeocodingService geocodingService;
  private final PriceSimulationService priceSimulationService;

  @Transactional(readOnly = true)
  public List<OrderDto> getAllOrders() {
    return orderRepository.findAll().stream()
        .map(orderMapper::toDto)
        .toList();
  }

  @Transactional
  public OrderDto createOrder(OrderDto orderDto) {
    Order order = orderMapper.toEntity(orderDto);

    if (order.getTrip() != null && order.getTrip().getId() == null) {
      order.setTrip(null);
    }
    if (order.getClient() != null && order.getClient().getId() == null) {
      order.setClient(null);
    }

    order.setStatus(OrderStatus.PENDING);

    // Géocoder l'adresse si les coordonnées ne sont pas fournies
    if (order.getLatitude() == null && order.getAddressText() != null) {
      double[] coords = geocodingService.geocode(order.getAddressText());
      if (coords != null) {
        order.setLatitude(coords[0]);
        order.setLongitude(coords[1]);
      }
    }

    // Calculer le prix estimé si non fourni
    if (order.getPrice() == null || order.getPrice() == 0.0) {
      order.setPrice(priceSimulationService.simulatePrice(order));
    }

    return orderMapper.toDto(orderRepository.save(order));
  }

  @Transactional(readOnly = true)
  public OrderDto getOrderById(Long id) {
    return orderRepository.findById(id)
        .map(orderMapper::toDto)
        .orElseThrow(() -> new RuntimeException("Commande non trouvée : " + id));
  }

  @Transactional
  public OrderDto updateOrder(Long id, OrderDto orderDto) {
    Order order = orderRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Commande non trouvée : " + id));
    order.setAddressText(orderDto.addressText());
    order.setLatitude(orderDto.latitude());
    order.setLongitude(orderDto.longitude());
    order.setRequestedDate(orderDto.requestedDate());
    order.setTimeSlot(orderDto.timeSlot());
    order.setPrice(orderDto.price());
    order.setQuantity(orderDto.quantity());
    if (orderDto.status() != null) order.setStatus(orderDto.status());
    return orderMapper.toDto(orderRepository.save(order));
  }

  @Transactional
  public void deleteOrder(Long id) {
    orderRepository.deleteById(id);
  }

  /** Simule le prix sans sauvegarder — pour l'affichage client avant confirmation. */
  @Transactional(readOnly = true)
  public PriceBreakdownDto simulatePriceBreakdown(OrderDto orderDto) {
    Order order = orderMapper.toEntity(orderDto);
    if (order.getClient() != null && order.getClient().getId() == null) order.setClient(null);
    if (order.getTrip()   != null && order.getTrip().getId()   == null) order.setTrip(null);
    return priceSimulationService.simulateBreakdown(order);
  }

  /** Commandes d'un client donné (espace client). */
  @Transactional(readOnly = true)
  public List<OrderDto> getByClientId(Long clientId) {
    return orderRepository.findByClientId(clientId).stream()
        .map(orderMapper::toDto)
        .toList();
  }
}
