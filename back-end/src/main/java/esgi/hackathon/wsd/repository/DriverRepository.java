package esgi.hackathon.wsd.repository;

import esgi.hackathon.wsd.entity.users.Driver;
import esgi.hackathon.wsd.enums.DriverStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
  List<Driver> findByStatus(DriverStatus status);
}
