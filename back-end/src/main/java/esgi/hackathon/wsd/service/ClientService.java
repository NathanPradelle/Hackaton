package esgi.hackathon.wsd.service;

import esgi.hackathon.wsd.dto.ClientDto;
import esgi.hackathon.wsd.mapper.ClientMapper;
import esgi.hackathon.wsd.entity.users.Client;
import esgi.hackathon.wsd.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

  private final ClientRepository clientRepository;
  private final ClientMapper clientMapper;

  @Transactional(readOnly = true)
  public List<ClientDto> getAllClients() {
    return clientRepository.findAll().stream().map(clientMapper::toDto).toList();
  }

  @Transactional(readOnly = true)
  public ClientDto getClientById(Long id) {
    return clientRepository.findById(id).map(clientMapper::toDto)
        .orElseThrow(() -> new RuntimeException("Client non trouvé : " + id));
  }

  @Transactional
  public ClientDto createClient(ClientDto clientDto) {
    Client client = clientMapper.toEntity(clientDto);
    return clientMapper.toDto(clientRepository.save(client));
  }
}