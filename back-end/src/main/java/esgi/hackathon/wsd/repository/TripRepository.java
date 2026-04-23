package esgi.hackathon.wsd.repository;

import esgi.hackathon.wsd.entity.operations.Trip;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
  List<Trip> findByDriverIdAndDate(Long driverId, LocalDate date);
}
