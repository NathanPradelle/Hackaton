package esgi.hackathon.wsd.mapper;

import esgi.hackathon.wsd.dto.TruckDto;
import esgi.hackathon.wsd.entity.logistics.Truck;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TruckMapper {

  @Mapping(source = "model.id", target = "modelId")
  TruckDto toDto(Truck truck);

  @Mapping(source = "modelId", target = "model.id")
  Truck toEntity(TruckDto truckDto);
}
