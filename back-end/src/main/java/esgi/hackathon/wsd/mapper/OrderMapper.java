package esgi.hackathon.wsd.mapper;

import esgi.hackathon.wsd.dto.OrderDto;
import esgi.hackathon.wsd.entity.operations.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

  @Mapping(source = "client.id", target = "clientId")
  @Mapping(source = "trip.id", target = "tripId")
  OrderDto toDto(Order order);

  @Mapping(source = "clientId", target = "client.id")
  @Mapping(source = "tripId", target = "trip.id")
  Order toEntity(OrderDto orderDto);
}
