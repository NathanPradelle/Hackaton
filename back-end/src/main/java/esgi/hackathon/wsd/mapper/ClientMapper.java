package esgi.hackathon.wsd.mapper;

import esgi.hackathon.wsd.dto.ClientDto;
import esgi.hackathon.wsd.entity.users.Client;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ClientMapper {

  @Mapping(source = "user.id", target = "userId")
  ClientDto toDto(Client client);

  @Mapping(source = "userId", target = "user.id")
  Client toEntity(ClientDto clientDto);
}
