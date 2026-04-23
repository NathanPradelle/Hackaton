package esgi.hackathon.wsd.entity.users;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Admin {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;
}
