package esgi.hackathon.wsd.dto;

import esgi.hackathon.wsd.enums.OrderStatus;
import java.time.LocalDate;

public record OrderDto(
    Long id,
    Long clientId,
    Long tripId,
    String addressText,
    Double latitude,
    Double longitude,
    LocalDate requestedDate,
    String timeSlot,
    Double price,
    Integer quantity,
    OrderStatus status
) {}