package esgi.hackathon.wsd.mapper;

import esgi.hackathon.wsd.dto.UserDto;
import esgi.hackathon.wsd.entity.users.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

  UserDto toDto(User user);

  User toEntity(UserDto userDto);
}