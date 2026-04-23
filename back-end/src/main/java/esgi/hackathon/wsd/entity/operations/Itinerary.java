package esgi.hackathon.wsd.entity.operations;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Itinerary {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "trip_id")
  private Trip trip;
  private Double duration;
  private String constraints;

  @Column(columnDefinition = "TEXT")
  private String gpsData;
}
