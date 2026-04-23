package esgi.hackathon.wsd.controller;

import esgi.hackathon.wsd.dto.ClientDto;
import esgi.hackathon.wsd.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClientController {

  private final ClientService clientService;

  @GetMapping
  public ResponseEntity<List<ClientDto>> getAllClients() {
    return ResponseEntity.ok(clientService.getAllClients());
  }

  @GetMapping("/{id}")
  public ResponseEntity<ClientDto> getClientById(@PathVariable Long id) {
    return ResponseEntity.ok(clientService.getClientById(id));
  }
}
