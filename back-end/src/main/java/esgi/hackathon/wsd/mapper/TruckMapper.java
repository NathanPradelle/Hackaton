package esgi.hackathon.wsd.mapper;

import esgi.hackathon.wsd.dto.TruckDto;
import esgi.hackathon.wsd.entity.logistics.Truck;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = VehicleModelMapper.class)
public interface TruckMapper {

  @Mapping(source = "model.id", target = "modelId")
  @Mapping(source = "model", target = "modele")
  TruckDto toDto(Truck truck);

  @Mapping(source = "modelId", target = "model.id")
  @Mapping(target = "model.brand", ignore = true)
  @Mapping(target = "model.modelName", ignore = true)
  @Mapping(target = "model.capacity", ignore = true)
  @Mapping(target = "model.fuelConsumption", ignore = true)
  @Mapping(target = "model.fuelType", ignore = true)
  @Mapping(target = "model.tankCapacity", ignore = true)
  Truck toEntity(TruckDto truckDto);
}
