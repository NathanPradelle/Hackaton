package esgi.hackathon.wsd.controller;

import esgi.hackathon.wsd.algorithm.dto.GenerationResultDto;
import esgi.hackathon.wsd.algorithm.service.ItineraryGenerationService;
import esgi.hackathon.wsd.dto.ItineraryDto;
import esgi.hackathon.wsd.service.ItineraryService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/itineraries")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ItineraryController {

    private final ItineraryService itineraryService;
    private final ItineraryGenerationService itineraryGenerationService;

    /**
     * Génère les itinéraires optimisés pour une date donnée (action admin).
     * Exemple : POST /api/itineraries/generate?date=2026-05-01
     */
    @PostMapping("/generate")
    public ResponseEntity<GenerationResultDto> generate(@RequestParam LocalDate date) {
        return new ResponseEntity<>(itineraryGenerationService.generateForDate(date), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ItineraryDto>> getAllItineraries() {
        return ResponseEntity.ok(itineraryService.getAllItineraries());
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<ItineraryDto> getByTripId(@PathVariable Long tripId) {
        return ResponseEntity.ok(itineraryService.getByTripId(tripId));
    }

    @PostMapping
    public ResponseEntity<ItineraryDto> createItinerary(@RequestBody ItineraryDto itineraryDto) {
        return new ResponseEntity<>(itineraryService.save(itineraryDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItineraryDto> updateItinerary(@PathVariable Long id, @RequestBody ItineraryDto itineraryDto) {
        return ResponseEntity.ok(itineraryService.update(id, itineraryDto));
    }
}
