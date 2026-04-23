package esgi.hackathon.wsd.mapper;

import esgi.hackathon.wsd.dto.TripDto;
import esgi.hackathon.wsd.entity.operations.Trip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TripMapper {

  @Mapping(source = "driver.id", target = "driverId")
  @Mapping(source = "truck.id", target = "truckId")
  TripDto toDto(Trip trip);

  @Mapping(source = "driverId", target = "driver.id")
  @Mapping(source = "truckId", target = "truck.id")
  Trip toEntity(TripDto tripDto);
}
