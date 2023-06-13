package com.api.parkingcontrol.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
