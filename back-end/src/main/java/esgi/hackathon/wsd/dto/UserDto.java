package esgi.hackathon.wsd.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import esgi.hackathon.wsd.enums.UserRole;

public record UserDto(
    Long id,
    @JsonProperty("identifiant") String username,
    UserRole role,
    @JsonProperty(value = "motDePasse", access = JsonProperty.Access.WRITE_ONLY) String password
) {}
