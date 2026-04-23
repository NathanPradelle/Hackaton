package esgi.hackathon.wsd.entity.operations;

import esgi.hackathon.wsd.entity.logistics.Truck;
import esgi.hackathon.wsd.entity.users.Driver;
import esgi.hackathon.wsd.enums.TripStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data;

@Entity
@Data
public class Trip {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "driver_id")
  private Driver driver;

  @ManyToOne
  @JoinColumn(name = "truck_id")
  private Truck truck;

  private LocalDate date;
  private String timeSlot;

  @Enumerated(EnumType.STRING)
  private TripStatus status;
}
