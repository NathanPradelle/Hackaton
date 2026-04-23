package esgi.hackathon.wsd.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OrderStatus {
    @JsonProperty("enAttente") PENDING,
    @JsonProperty("confirmee") ASSIGNED,
    @JsonProperty("enCours") PICKED_UP,
    @JsonProperty("livree") DELIVERED,
    @JsonProperty("annulee") CANCELLED
}
