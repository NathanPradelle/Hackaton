package esgi.hackathon.wsd.controller;

import esgi.hackathon.wsd.algorithm.dto.PriceBreakdownDto;
import esgi.hackathon.wsd.dto.OrderDto;
import esgi.hackathon.wsd.service.OrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {

  private final OrderService orderService;

  /** Simule le prix sans sauvegarder — appelé avant confirmation client. */
  @PostMapping("/simulate")
  public ResponseEntity<PriceBreakdownDto> simulate(@RequestBody OrderDto orderDto) {
    return ResponseEntity.ok(orderService.simulatePriceBreakdown(orderDto));
  }

  /** Commandes d'un client spécifique (espace client). */
  @GetMapping("/client/{clientId}")
  public ResponseEntity<List<OrderDto>> getByClient(@PathVariable Long clientId) {
    return ResponseEntity.ok(orderService.getByClientId(clientId));
  }

  @GetMapping
  public ResponseEntity<List<OrderDto>> getAllOrders() {
    return ResponseEntity.ok(orderService.getAllOrders());
  }

  @GetMapping("/{id}")
  public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
    return ResponseEntity.ok(orderService.getOrderById(id));
  }

  @PostMapping
  public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto orderDto) {
    OrderDto createdOrder = orderService.createOrder(orderDto);
    return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<OrderDto> updateOrder(@PathVariable Long id, @RequestBody OrderDto orderDto) {
    return ResponseEntity.ok(orderService.updateOrder(id, orderDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
    orderService.deleteOrder(id);
    return ResponseEntity.ok().build();
  }
}
