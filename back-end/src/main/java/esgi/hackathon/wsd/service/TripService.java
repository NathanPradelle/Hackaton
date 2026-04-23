package esgi.hackathon.wsd.service;

import esgi.hackathon.wsd.dto.TripDto;
import esgi.hackathon.wsd.mapper.TripMapper;
import esgi.hackathon.wsd.entity.operations.Trip;
import esgi.hackathon.wsd.enums.TripStatus;
import esgi.hackathon.wsd.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripService {

  private final TripRepository tripRepository;
  private final TripMapper tripMapper;

  @Transactional(readOnly = true)
  public List<TripDto> getAllTrips() {
    return tripRepository.findAll().stream().map(tripMapper::toDto).toList();
  }

  @Transactional
  public TripDto createTrip(TripDto tripDto) {
    Trip trip = tripMapper.toEntity(tripDto);

    if (trip.getDriver() != null && trip.getDriver().getId() == null) {
      trip.setDriver(null);
    }
    if (trip.getTruck() != null && trip.getTruck().getId() == null) {
      trip.setTruck(null);
    }

    trip.setStatus(TripStatus.PLANNED);
    return tripMapper.toDto(tripRepository.save(trip));
  }
}
