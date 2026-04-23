package esgi.hackathon.wsd.controller;

import esgi.hackathon.wsd.algorithm.service.GeocodingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/geocode")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GeocodingController {

    private final GeocodingService geocodingService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> geocode(@RequestParam String address) {
        double[] coords = geocodingService.geocode(address);
        if (coords == null) {
            return ResponseEntity.ok(Map.of("found", false));
        }
        return ResponseEntity.ok(Map.of(
            "found", true,
            "lat",   coords[0],
            "lng",   coords[1]
        ));
    }
}
