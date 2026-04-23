package esgi.hackathon.wsd.controller;

import esgi.hackathon.wsd.dto.TripDto;
import esgi.hackathon.wsd.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TripController {

  private final TripService tripService;

  @GetMapping
  public ResponseEntity<List<TripDto>> getAllTrips() {
    return ResponseEntity.ok(tripService.getAllTrips());
  }
}
