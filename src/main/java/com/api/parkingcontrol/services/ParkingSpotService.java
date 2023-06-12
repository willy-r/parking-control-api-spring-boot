package com.api.parkingcontrol.services;

import com.api.parkingcontrol.dtos.ParkingSpotDTO;
import com.api.parkingcontrol.entities.ParkingSpot;
import com.api.parkingcontrol.repositories.ParkingSpotRepository;
import com.api.parkingcontrol.services.exceptions.ObjectConflictException;
import com.api.parkingcontrol.services.exceptions.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@Service
public class ParkingSpotService {
    final ParkingSpotRepository parkingSpotRepository;

    public ParkingSpotService(ParkingSpotRepository parkingSpotRepository) {
        this.parkingSpotRepository = parkingSpotRepository;
    }

    public Page<ParkingSpot> findAll(Pageable pageable) {
        return parkingSpotRepository.findAll(pageable);
    }

    public ParkingSpot findById(UUID id) {
        Optional<ParkingSpot> parkingSpotOptional = parkingSpotRepository.findById(id);
        return parkingSpotOptional.orElseThrow(() -> new ObjectNotFoundException("Object with identifier " + id + " not found."));
    }

    @Transactional
    public ParkingSpot save(ParkingSpotDTO parkingSpotDTO) {
        if (existsByLicensePlateCar(parkingSpotDTO.getLicensePlateCar())) {
            throw new ObjectConflictException("License Plate Car " +  parkingSpotDTO.getLicensePlateCar() + " is already in use");
        }
        if (existsByParkingSpotNumber(parkingSpotDTO.getParkingSpotNumber())) {
            throw new ObjectConflictException("Parking Spot " + parkingSpotDTO.getParkingSpotNumber() + " is already in use");
        }
        if (existsByApartmentAndBlock(parkingSpotDTO.getApartment(), parkingSpotDTO.getBlock())) {
            throw new ObjectConflictException("Parking Spot already registered for apartment " + parkingSpotDTO.getApartment() + " and block " + parkingSpotDTO.getBlock());
        }
        var parkingSpotEntity = new ParkingSpot();
        BeanUtils.copyProperties(parkingSpotDTO, parkingSpotEntity);
        parkingSpotEntity.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
        return parkingSpotRepository.save(parkingSpotEntity);
    }

    public boolean existsByLicensePlateCar(String licensePlateCar) {
        return parkingSpotRepository.existsByLicensePlateCar(licensePlateCar);
    }

    public boolean existsByParkingSpotNumber(String parkingSpotNumber) {
        return parkingSpotRepository.existsByParkingSpotNumber(parkingSpotNumber);
    }

    public boolean existsByApartmentAndBlock(String apartment, String block) {
        return parkingSpotRepository.existsByApartmentAndBlock(apartment, block);
    }

    @Transactional
    public void delete(UUID id) {
        findById(id);
        parkingSpotRepository.deleteById(id);
    }

    @Transactional
    public ParkingSpot update(UUID id, ParkingSpotDTO parkingSpotDTO) {

    }
}
