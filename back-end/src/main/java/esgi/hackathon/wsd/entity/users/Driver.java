package esgi.hackathon.wsd.entity.users;

import esgi.hackathon.wsd.enums.DriverStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class Driver {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;

  private String firstName;
  private String lastName;
  private String licenseNumber;
  @Enumerated(EnumType.STRING)
  private DriverStatus status; // Ex: AVAILABLE, ON_TRIP, AWAY
}
