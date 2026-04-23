package esgi.hackathon.wsd.mapper;

import esgi.hackathon.wsd.dto.ItineraryDto;
import esgi.hackathon.wsd.entity.operations.Itinerary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItineraryMapper {

  @Mapping(source = "trip.id", target = "tripId")
  ItineraryDto toDto(Itinerary itinerary);

  @Mapping(source = "tripId", target = "trip.id")
  Itinerary toEntity(ItineraryDto itineraryDto);
}
