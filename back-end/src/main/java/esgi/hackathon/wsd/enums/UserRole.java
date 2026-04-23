package esgi.hackathon.wsd.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum UserRole {
    @JsonProperty("admin") ADMIN,
    @JsonProperty("chauffeur") DRIVER,
    @JsonProperty("client") CLIENT
}
