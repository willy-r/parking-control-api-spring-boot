package com.api.parkingcontrol.controllers;

import com.api.parkingcontrol.dtos.ParkingSpotDTO;
import com.api.parkingcontrol.entities.ParkingSpot;
import com.api.parkingcontrol.services.ParkingSpotService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/parking-spot")
public class ParkingSpotController {
    final ParkingSpotService parkingSpotService;

    public ParkingSpotController(ParkingSpotService parkingSpotService) {
        this.parkingSpotService = parkingSpotService;
    }

    @GetMapping
    public ResponseEntity<Page<ParkingSpot>> getAll(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ParkingSpot> parkingSpotPageable = parkingSpotService.findAll(pageable);
        return ResponseEntity.ok().body(parkingSpotPageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingSpot> getById(@PathVariable UUID id) {
        var parkingSpotEntity = parkingSpotService.findById(id);
        return ResponseEntity.ok().body(parkingSpotEntity);
    }

    @PostMapping
    public ResponseEntity<ParkingSpot> save(@RequestBody @Valid ParkingSpotDTO parkingSpotDTO) {
        var parkingSpotEntity = parkingSpotService.save(parkingSpotDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotEntity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        parkingSpotService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParkingSpot> update(@PathVariable UUID id, @RequestBody @Valid ParkingSpotDTO parkingSpotDTO) {
        Optional<ParkingSpot> parkingSpotOptional = parkingSpotService.findById(id);
        if (parkingSpotOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found");
        }
        var parkingSpotEntity = new ParkingSpot();
        BeanUtils.copyProperties(parkingSpotDTO, parkingSpotEntity);
        parkingSpotEntity.setId(parkingSpotOptional.get().getId());
        parkingSpotEntity.setRegistrationDate(parkingSpotOptional.get().getRegistrationDate());
        var parkingSpotDatabase = parkingSpotService.save(parkingSpotEntity);
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotDatabase);
    }
}
