package esgi.hackathon.wsd.dto;

public record ClientDto(
    Long id,
    Long userId,
    String name,
    String siretNumber,
    String city
) {}