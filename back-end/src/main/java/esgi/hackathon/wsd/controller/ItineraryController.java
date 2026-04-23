package esgi.hackathon.wsd.controller;

import esgi.hackathon.wsd.dto.ItineraryDto;
import esgi.hackathon.wsd.service.ItineraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/itineraries")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ItineraryController {

    private final ItineraryService itineraryService;

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
