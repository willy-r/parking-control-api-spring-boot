package com.api.parkingcontrol.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ParkingSpotDTO {
    @NotBlank
    private String parkingSpotNumber;
    @NotBlank
    @Size(max = 7)
    private String licensePlateCar;
    @NotBlank
    private String brandCar;
    @NotBlank
    private String modelCar;
    @NotBlank
    private String colorCar;
    @NotBlank
    private String responsibleName;
    @NotBlank
    private String apartment;
    @NotBlank
    private String block;

    public ParkingSpotDTO(String parkingSpotNumber, String licensePlateCar, String brandCar, String modelCar, String colorCar, String responsibleName, String apartment, String block) {
        this.parkingSpotNumber = parkingSpotNumber;
        this.licensePlateCar = licensePlateCar;
        this.brandCar = brandCar;
        this.modelCar = modelCar;
        this.colorCar = colorCar;
        this.responsibleName = responsibleName;
        this.apartment = apartment;
        this.block = block;
    }
}
