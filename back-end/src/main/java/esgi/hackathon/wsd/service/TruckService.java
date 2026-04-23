package esgi.hackathon.wsd.service;

import esgi.hackathon.wsd.dto.TruckDto;
import esgi.hackathon.wsd.mapper.TruckMapper;
import esgi.hackathon.wsd.entity.logistics.Truck;
import esgi.hackathon.wsd.enums.TruckStatus;
import esgi.hackathon.wsd.repository.TruckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TruckService {

  private final TruckRepository truckRepository;
  private final TruckMapper truckMapper;

  @Transactional(readOnly = true)
  public List<TruckDto> getAllTrucks() {
    return truckRepository.findAll().stream().map(truckMapper::toDto).toList();
  }

  @Transactional(readOnly = true)
  public List<TruckDto> getAvailableTrucks() {
    return truckRepository.findByStatus(TruckStatus.AVAILABLE).stream()
        .map(truckMapper::toDto).toList();
  }

  @Transactional
  public TruckDto createTruck(TruckDto truckDto) {
    Truck truck = truckMapper.toEntity(truckDto);

    if (truck.getModel() != null && truck.getModel().getId() == null) {
      truck.setModel(null);
    }

    truck.setStatus(TruckStatus.AVAILABLE);
    return truckMapper.toDto(truckRepository.save(truck));
  }

  @Transactional
  public TruckDto updateTruck(Long id, TruckDto truckDto) {
    Truck truck = truckRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Camion non trouvé : " + id));
    truck.setLicensePlate(truckDto.licensePlate());
    truck.setCurrentFuelLevel(truckDto.currentFuelLevel());
    if (truckDto.status() != null) truck.setStatus(truckDto.status());
    return truckMapper.toDto(truckRepository.save(truck));
  }

  @Transactional
  public void deleteTruck(Long id) {
    truckRepository.deleteById(id);
  }
}
