package com.api.parkingcontrol;

import com.api.parkingcontrol.dtos.ParkingSpotDTO;
import com.api.parkingcontrol.entities.ParkingSpot;
import com.api.parkingcontrol.repositories.ParkingSpotRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Test;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = ParkingControlApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class ParkingSpotRestControllerIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired ParkingSpotRepository parkingSpotRepository;

    @After
    public void tearDown() {
        parkingSpotRepository.deleteAll();
    }

    @Test
    public void givenParkingSpots_whenGetParkingSpots_thenStatus200() throws Exception {
        var parkingSpotDTO1 = new ParkingSpotDTO("2058", "RRS8562", "Audi", "Q5", "Black", "Test", "265", "8");
        var parkingSpotDTO2 = new ParkingSpotDTO("2057", "RRS8561", "Audi", "Q5", "Black", "Test", "264", "7");
        ParkingSpot parkingSpotEntity1 = createTestParkingSpot(parkingSpotDTO1);
        ParkingSpot parkingSpotEntity2 = createTestParkingSpot(parkingSpotDTO2);

        mvc.perform(get("/parking-spot").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content", hasSize(equalTo(2))));

        List<ParkingSpot> allParkingSpot = parkingSpotRepository.findAll();
        assertThat(allParkingSpot).hasSize(2);
        assertThat(allParkingSpot).extracting(ParkingSpot::getParkingSpotNumber).isEqualTo(List.of("2058", "2057"));
    }

    @Test
    public void givenParkingSpotId_whenGetParkingSpot_thenStatus200() throws Exception {
        var parkingSpotDTO = new ParkingSpotDTO("2058", "RRS8562", "Audi", "Q5", "Black", "Test", "265", "8");
        ParkingSpot parkingSpotEntity = createTestParkingSpot(parkingSpotDTO);

        mvc.perform(get("/parking-spot/" + parkingSpotEntity.getId()).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(parkingSpotEntity.getId().toString())));

        List<ParkingSpot> allParkingSpot = parkingSpotRepository.findAll();
        assertThat(allParkingSpot).hasSize(1);
    }

    @Test
    public void givenNonExistingParkingSpotId_whenGetParkingSpot_thenStatus404() throws Exception {
        mvc.perform(get("/parking-spot/" + UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
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
            .andExpect(status().isConflict())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    public void givenAlreadyCreatedParkingSpotNumber_whenCreateNewParkingSpot_thenStatus409() throws Exception {
        var parkingSpotDTO = new ParkingSpotDTO("2058", "RRS8562", "Audi", "Q5", "Black", "Test", "265", "8");
        ParkingSpot parkingSpotEntity = createTestParkingSpot(parkingSpotDTO);

        parkingSpotDTO.setParkingSpotNumber(parkingSpotEntity.getParkingSpotNumber());
        String requestJson = new ObjectMapper().writeValueAsString(parkingSpotDTO);
        mvc.perform(post("/parking-spot").contentType(MediaType.APPLICATION_JSON).content(requestJson))
            .andExpect(status().isConflict())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    public void givenAlreadyCreatedApartmentAndBlock_whenCreateNewParkingSpot_thenStatus409() throws Exception {
        var parkingSpotDTO = new ParkingSpotDTO("2058", "RRS8562", "Audi", "Q5", "Black", "Test", "265", "8");
        ParkingSpot parkingSpotEntity = createTestParkingSpot(parkingSpotDTO);

        parkingSpotDTO.setApartment(parkingSpotEntity.getApartment());
        parkingSpotDTO.setBlock(parkingSpotEntity.getBlock());
        String requestJson = new ObjectMapper().writeValueAsString(parkingSpotDTO);
        mvc.perform(post("/parking-spot").contentType(MediaType.APPLICATION_JSON).content(requestJson))
            .andExpect(status().isConflict())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    public void givenParkingSpotId_whenDeleteParkingSpot_thenDeleteParkingSpot() throws Exception {
        var parkingSpotDTO = new ParkingSpotDTO("2058", "RRS8562", "Audi", "Q5", "Black", "Test", "265", "8");
        ParkingSpot parkingSpotEntity = createTestParkingSpot(parkingSpotDTO);

        mvc.perform(delete("/parking-spot/" + parkingSpotEntity.getId()).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        List<ParkingSpot> allParkingSpot = parkingSpotRepository.findAll();
        assertThat(allParkingSpot).hasSize(0);
    }

    @Test
    public void givenNonExistingParkingSpotId_whenDeleteParkingSot_thenStatus404() throws Exception {
        mvc.perform(delete("/parking-spot/" + UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    public void givenParkingSpotIdIdWithValidInput_whenUpdateParkingSpot_thenUpdateParkingSpot() throws Exception {
        var parkingSpotDTO = new ParkingSpotDTO("2058", "RRS8562", "Audi", "Q5", "Black", "Test", "265", "8");
        ParkingSpot parkingSpotEntity = createTestParkingSpot(parkingSpotDTO);

        parkingSpotDTO.setParkingSpotNumber("2057");
        String requestJson = new ObjectMapper().writeValueAsString(parkingSpotDTO);
        mvc.perform(put("/parking-spot/" + parkingSpotEntity.getId()).contentType(MediaType.APPLICATION_JSON).content(requestJson))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        List<ParkingSpot> allParkingSpot = parkingSpotRepository.findAll();
        assertThat(allParkingSpot).extracting(ParkingSpot::getParkingSpotNumber).containsOnly(parkingSpotDTO.getParkingSpotNumber());
        assertThat(allParkingSpot).extracting(ParkingSpot::getId).containsOnly(parkingSpotEntity.getId());
    }

    @Test
    public void givenParkingSpotIdIdWithInvalidInput_whenUpdateParkingSpot_thenStatus400() throws Exception {
        var parkingSpotDTO = new ParkingSpotDTO("2058", "RRS8562", "Audi", "Q5", "Black", "Test", "265", "8");
        ParkingSpot parkingSpotEntity = createTestParkingSpot(parkingSpotDTO);

        parkingSpotDTO.setParkingSpotNumber("");
        String requestJson = new ObjectMapper().writeValueAsString(parkingSpotDTO);
        mvc.perform(put("/parking-spot/" + parkingSpotEntity.getId()).contentType(MediaType.APPLICATION_JSON).content(requestJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void givenNonExistingParkingSpotId_whenUpdateParkingSpot_thenStatus404() throws Exception {
        var parkingSpotDTO = new ParkingSpotDTO("2058", "RRS8562", "Audi", "Q5", "Black", "Test", "265", "8");
        String requestJson = new ObjectMapper().writeValueAsString(parkingSpotDTO);
        mvc.perform(put("/parking-spot/" + UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON).content(requestJson))
            .andExpect(status().isNotFound())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    public void givenNonExistingParkingSpotIdWithInvalidInput_whenUpdateParkingSpot_thenStatus400() throws Exception {
        var parkingSpotDTO = new ParkingSpotDTO("", "RRS8562", "Audi", "Q5", "Black", "Test", "265", "8");
        String requestJson = new ObjectMapper().writeValueAsString(parkingSpotDTO);
        mvc.perform(put("/parking-spot/" + UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON).content(requestJson))
            .andExpect(status().isBadRequest());
    }

    private ParkingSpot createTestParkingSpot(ParkingSpotDTO parkingSpotDTO) {
        var parkingSpotEntity = new ParkingSpot();
        BeanUtils.copyProperties(parkingSpotDTO, parkingSpotEntity);
        parkingSpotEntity.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
        return parkingSpotRepository.saveAndFlush(parkingSpotEntity);
    }
}
