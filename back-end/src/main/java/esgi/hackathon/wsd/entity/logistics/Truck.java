package esgi.hackathon.wsd.entity.logistics;

import esgi.hackathon.wsd.enums.TruckStatus;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Truck {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "model_id")
  private VehicleModel model;

  @Enumerated(EnumType.STRING)
  private TruckStatus status;

  private String licensePlate;
  private Double currentFuelLevel;
}
