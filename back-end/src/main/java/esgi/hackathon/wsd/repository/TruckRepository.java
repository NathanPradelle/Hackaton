package esgi.hackathon.wsd.repository;

import esgi.hackathon.wsd.entity.logistics.Truck;
import esgi.hackathon.wsd.enums.TruckStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TruckRepository extends JpaRepository<Truck, Long> {
  List<Truck> findByStatus(TruckStatus status);

  Truck findFirstByStatusAndModelFuelConsumptionGreaterThan(TruckStatus status, Double minConsumption);
}
