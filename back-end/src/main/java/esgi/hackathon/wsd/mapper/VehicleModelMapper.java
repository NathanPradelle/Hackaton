package esgi.hackathon.wsd.mapper;

import esgi.hackathon.wsd.dto.VehicleModelDto;
import esgi.hackathon.wsd.entity.logistics.VehicleModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VehicleModelMapper {

  VehicleModelDto toDto(VehicleModel vehicleModel);

  VehicleModel toEntity(VehicleModelDto vehicleModelDto);
}
