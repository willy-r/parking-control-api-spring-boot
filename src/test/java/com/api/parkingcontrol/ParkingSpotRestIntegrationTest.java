package com.api.parkingcontrol;

import com.api.parkingcontrol.dtos.ParkingSpotDTO;
import com.api.parkingcontrol.entities.ParkingSpot;
import com.api.parkingcontrol.repositories.ParkingSpotRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = ParkingControlApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class ParkingSpotRestIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired ParkingSpotRepository parkingSpotRepository;

    @After
    public void tearDown() {
        parkingSpotRepository.deleteAll();
    }

    @Test
    public void givenValidInput_whenCreateNewParkingSpot_thenCreateParkingSpot() throws Exception {
        var parkingSpotDTO = new ParkingSpotDTO("2058", "RRS8562", "Audi", "Q5", "Black", "Test", "265", "8");
        String requestJson = new ObjectMapper().writeValueAsString(parkingSpotDTO);
        mvc.perform(post("/parking-spot").contentType(MediaType.APPLICATION_JSON).content(requestJson))
            .andExpect(status().isCreated())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        List<ParkingSpot> allParkingSpot = parkingSpotRepository.findAll();
        assertThat(allParkingSpot).hasSize(1);
    }

    @Test
    public void givenInvalidInput_whenCreateNewParkingSpot_thenStatus400() throws Exception {
        var parkingSpotDTO = new ParkingSpotDTO("", "RRS8562", "Audi", "Q5", "Black", "Test", "265", "8");
        String requestJson = new ObjectMapper().writeValueAsString(parkingSpotDTO);
        mvc.perform(post("/parking-spot").contentType(MediaType.APPLICATION_JSON).content(requestJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void givenAlreadyCreatedLicensePlateCar_whenCreateNewParkingSpot_thenStatus409() throws Exception {
        var parkingSpotDTO = new ParkingSpotDTO("2058", "RRS8562", "Audi", "Q5", "Black", "Test", "265", "8");
        ParkingSpot parkingSpotEntity = createTestParkingSpot(parkingSpotDTO);

        parkingSpotDTO.setLicensePlateCar(parkingSpotEntity.getLicensePlateCar());
        String requestJson = new ObjectMapper().writeValueAsString(parkingSpotDTO);
        mvc.perform(post("/parking-spot").contentType(MediaType.APPLICATION_JSON).content(requestJson))
            .andExpect(status().isConflict());
    }

    @Test
    public void givenAlreadyCreatedParkingSpotNumber_whenCreateNewParkingSpot_thenStatus409() throws Exception {
        var parkingSpotDTO = new ParkingSpotDTO("2058", "RRS8562", "Audi", "Q5", "Black", "Test", "265", "8");
        ParkingSpot parkingSpotEntity = createTestParkingSpot(parkingSpotDTO);

        parkingSpotDTO.setParkingSpotNumber(parkingSpotEntity.getParkingSpotNumber());
        String requestJson = new ObjectMapper().writeValueAsString(parkingSpotDTO);
        mvc.perform(post("/parking-spot").contentType(MediaType.APPLICATION_JSON).content(requestJson))
            .andExpect(status().isConflict());
    }

    @Test
    public void givenAlreadyCreatedApartmentAndBlock_whenCreateNewParkingSpot_thenStatus409() throws Exception {
        var parkingSpotDTO = new ParkingSpotDTO("2058", "RRS8562", "Audi", "Q5", "Black", "Test", "265", "8");
        ParkingSpot parkingSpotEntity = createTestParkingSpot(parkingSpotDTO);

        parkingSpotDTO.setApartment(parkingSpotEntity.getApartment());
        parkingSpotDTO.setBlock(parkingSpotEntity.getBlock());
        String requestJson = new ObjectMapper().writeValueAsString(parkingSpotDTO);
        mvc.perform(post("/parking-spot").contentType(MediaType.APPLICATION_JSON).content(requestJson))
            .andExpect(status().isConflict());
    }

    private ParkingSpot createTestParkingSpot(ParkingSpotDTO parkingSpotDTO) {
        var parkingSpotEntity = new ParkingSpot();
        BeanUtils.copyProperties(parkingSpotDTO, parkingSpotEntity);
        parkingSpotEntity.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
        return parkingSpotRepository.saveAndFlush(parkingSpotEntity);
    }
}
