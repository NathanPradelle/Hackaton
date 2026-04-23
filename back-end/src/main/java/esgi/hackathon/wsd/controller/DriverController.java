package esgi.hackathon.wsd.controller;

import esgi.hackathon.wsd.dto.DriverDto;
import esgi.hackathon.wsd.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DriverController {

  private final DriverService driverService;

  @GetMapping
  public ResponseEntity<List<DriverDto>> getAllDrivers() {
    return ResponseEntity.ok(driverService.getAllDrivers());
  }


  @GetMapping("/available")
  public ResponseEntity<List<DriverDto>> getAvailableDrivers() {
    return ResponseEntity.ok(driverService.getAvailableDrivers());
  }
}
