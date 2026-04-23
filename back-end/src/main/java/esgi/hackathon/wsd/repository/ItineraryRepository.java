package esgi.hackathon.wsd.repository;

import esgi.hackathon.wsd.entity.operations.Itinerary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItineraryRepository extends JpaRepository<Itinerary, Long> {
  Optional<Itinerary> findByTrip_Id(Long tripId);
}
