package esgi.hackathon.wsd.dto;

public record RegisterRequestDto(
    String name,
    String siret,
    String city,
    String identifier,
    String password
) {}
