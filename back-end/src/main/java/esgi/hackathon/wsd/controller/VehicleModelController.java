package esgi.hackathon.wsd.controller;

import esgi.hackathon.wsd.dto.VehicleModelDto;
import esgi.hackathon.wsd.service.VehicleModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/models")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VehicleModelController {

  private final VehicleModelService vehicleModelService;

  @GetMapping
  public ResponseEntity<List<VehicleModelDto>> getAllModels() {
    return ResponseEntity.ok(vehicleModelService.getAllModels());
  }
}
