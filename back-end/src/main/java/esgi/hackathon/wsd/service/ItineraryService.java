package esgi.hackathon.wsd.service;

import esgi.hackathon.wsd.dto.ItineraryDto;
import esgi.hackathon.wsd.entity.operations.Itinerary;
import esgi.hackathon.wsd.mapper.ItineraryMapper;
import esgi.hackathon.wsd.repository.ItineraryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItineraryService {

    private final ItineraryRepository itineraryRepository;
    private final ItineraryMapper itineraryMapper;

    @Transactional(readOnly = true)
    public List<ItineraryDto> getAllItineraries() {
        return itineraryRepository.findAll().stream()
            .map(itineraryMapper::toDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public ItineraryDto getByTripId(Long tripId) {
        return itineraryRepository.findByTrip_Id(tripId)
            .map(itineraryMapper::toDto)
            .orElseThrow(() -> new RuntimeException("Itinéraire non trouvé pour la tournée : " + tripId));
    }

    @Transactional
    public ItineraryDto save(ItineraryDto itineraryDto) {
        Itinerary itinerary = itineraryMapper.toEntity(itineraryDto);
        if (itinerary.getTrip() != null && itinerary.getTrip().getId() == null) {
            itinerary.setTrip(null);
        }
        return itineraryMapper.toDto(itineraryRepository.save(itinerary));
    }

    @Transactional
    public ItineraryDto update(Long id, ItineraryDto itineraryDto) {
        Itinerary itinerary = itineraryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Itinéraire non trouvé : " + id));
        itinerary.setDuration(itineraryDto.duration());
        itinerary.setConstraints(itineraryDto.constraints());
        itinerary.setGpsData(itineraryDto.gpsData());
        return itineraryMapper.toDto(itineraryRepository.save(itinerary));
    }
}
