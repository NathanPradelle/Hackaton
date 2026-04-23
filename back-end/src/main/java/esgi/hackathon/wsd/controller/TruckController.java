package esgi.hackathon.wsd.controller;


import esgi.hackathon.wsd.dto.TruckDto;
import esgi.hackathon.wsd.service.TruckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trucks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TruckController {

  private final TruckService truckService;

  @GetMapping
  public ResponseEntity<List<TruckDto>> getAllTrucks() {
    return ResponseEntity.ok(truckService.getAllTrucks());
  }

  @GetMapping("/available")
  public ResponseEntity<List<TruckDto>> getAvailableTrucks() {
    return ResponseEntity.ok(truckService.getAvailableTrucks());
  }
}
