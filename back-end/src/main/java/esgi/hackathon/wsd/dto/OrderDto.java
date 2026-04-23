package esgi.hackathon.wsd.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import esgi.hackathon.wsd.enums.OrderStatus;
import java.time.LocalDate;

public record OrderDto(
    Long id,
    Long clientId,
    @JsonProperty("tourneeId") Long tripId,
    @JsonProperty("adresseTexte") String addressText,
    Double latitude,
    Double longitude,
    @JsonProperty("dateVoulu") LocalDate requestedDate,
    @JsonProperty("plageHoraire") String timeSlot,
    @JsonProperty("prix") Double price,
    @JsonProperty("quantite") Integer quantity,
    @JsonProperty("statut") OrderStatus status
) {}
