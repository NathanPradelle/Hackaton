package esgi.hackathon.wsd.algorithm.dto;

import java.util.List;

public record GenerationResultDto(
    int routesCrees,
    List<Long> tripIds,
    int commandesAssignees
) {}
