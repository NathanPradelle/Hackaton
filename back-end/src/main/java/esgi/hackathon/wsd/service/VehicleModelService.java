package esgi.hackathon.wsd.service;

import esgi.hackathon.wsd.dto.VehicleModelDto;
import esgi.hackathon.wsd.mapper.VehicleModelMapper;
import esgi.hackathon.wsd.entity.logistics.VehicleModel;
import esgi.hackathon.wsd.repository.VehicleModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleModelService {

  private final VehicleModelRepository vehicleModelRepository;
  private final VehicleModelMapper vehicleModelMapper;

  @Transactional(readOnly = true)
  public List<VehicleModelDto> getAllModels() {
    return vehicleModelRepository.findAll().stream().map(vehicleModelMapper::toDto).toList();
  }

  @Transactional
  public VehicleModelDto createModel(VehicleModelDto dto) {
    VehicleModel model = vehicleModelMapper.toEntity(dto);
    return vehicleModelMapper.toDto(vehicleModelRepository.save(model));
  }
}
