package esgi.hackathon.wsd.dto;

public record AuthResponseDto(String token, AuthUserDto user) {

    public record AuthUserDto(
        Long id,
        String identifier,
        String name,
        String siret,
        String city,
        Long clientId
    ) {}
}
