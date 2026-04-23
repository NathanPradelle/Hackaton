package esgi.hackathon.wsd.repository;

import esgi.hackathon.wsd.entity.operations.Order;
import esgi.hackathon.wsd.enums.OrderStatus;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
  List<Order> findByStatus(OrderStatus status);

  List<Order> findByStatusAndRequestedDate(OrderStatus status, LocalDate date);

  List<Order> findByClientId(Long clientId);
}
