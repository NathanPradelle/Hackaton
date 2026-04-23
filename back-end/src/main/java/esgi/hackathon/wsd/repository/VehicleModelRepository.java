package esgi.hackathon.wsd.repository;

import esgi.hackathon.wsd.entity.logistics.VehicleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleModelRepository extends JpaRepository<VehicleModel, Long> {
}
