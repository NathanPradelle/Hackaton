package esgi.hackathon.wsd.mapper;

import esgi.hackathon.wsd.dto.DriverDto;
import esgi.hackathon.wsd.entity.users.Driver;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface DriverMapper {

  @Mapping(source = "user.id", target = "userId")
  DriverDto toDto(Driver driver);

  @Mapping(source = "userId", target = "user.id")
  Driver toEntity(DriverDto driverDto);
}
