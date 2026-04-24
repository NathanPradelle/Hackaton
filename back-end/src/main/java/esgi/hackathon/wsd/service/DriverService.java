package esgi.hackathon.wsd.service;


import esgi.hackathon.wsd.dto.DriverDto;
import esgi.hackathon.wsd.mapper.DriverMapper;
import esgi.hackathon.wsd.entity.users.Driver;
import esgi.hackathon.wsd.enums.DriverStatus;
import esgi.hackathon.wsd.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverService {

  private final DriverRepository driverRepository;
  private final DriverMapper driverMapper;

  @Transactional(readOnly = true)
  public List<DriverDto> getAllDrivers() {
    return driverRepository.findAll().stream().map(driverMapper::toDto).toList();
  }

  @Transactional(readOnly = true)
  public List<DriverDto> getAvailableDrivers() {
    return driverRepository.findByStatus(DriverStatus.AVAILABLE).stream()
        .map(driverMapper::toDto).toList();
  }

  @Transactional
  public DriverDto createDriver(DriverDto driverDto) {
    Driver driver = driverMapper.toEntity(driverDto);
    driver.setStatus(DriverStatus.AVAILABLE);
    return driverMapper.toDto(driverRepository.save(driver));
  }
}