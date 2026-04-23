package esgi.hackathon.wsd.mapper;

import esgi.hackathon.wsd.dto.UserDto;
import esgi.hackathon.wsd.entity.users.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

  UserDto toDto(User user);

  @Mapping(target = "password", ignore = true)
  User toEntity(UserDto userDto);
}