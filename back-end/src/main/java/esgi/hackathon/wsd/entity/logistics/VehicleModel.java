package esgi.hackathon.wsd.entity.logistics;


import esgi.hackathon.wsd.enums.FuelType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class VehicleModel {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String brand;
  private String modelName;
  private Double capacity; // Volume ou poids max
  private Double fuelConsumption;

  @Enumerated(EnumType.STRING)
  private FuelType fuelType;

  private Double tankCapacity;
}
