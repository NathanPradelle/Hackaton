package esgi.hackathon.wsd.entity.operations;

import esgi.hackathon.wsd.entity.users.Client;
import esgi.hackathon.wsd.enums.OrderStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data;

@Entity
@Data
@Table(name = "orders")
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "client_id")
  private Client client;

  @ManyToOne
  @JoinColumn(name = "trip_id")
  private Trip trip;

  private String addressText;
  private Double latitude;
  private Double longitude;
  private LocalDate requestedDate;
  private String timeSlot;
  private Double price;
  private Integer quantity;

  @Enumerated(EnumType.STRING)
  private OrderStatus status;
}
