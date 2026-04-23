package esgi.hackathon.wsd.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ClientDto(
    Long id,
    Long userId,
    @JsonProperty("nom") String name,
    @JsonProperty("numeroSiret") String siretNumber,
    String city
) {}
