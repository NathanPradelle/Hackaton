package esgi.hackathon.wsd.dto;

import esgi.hackathon.wsd.enums.UserRole;

public record UserDto(
    Long id,
    String username,
    UserRole role
) {}
