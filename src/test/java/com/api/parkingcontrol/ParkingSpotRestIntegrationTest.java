package com.api.parkingcontrol;

import com.api.parkingcontrol.entities.ParkingSpot;
import com.api.parkingcontrol.repositories.ParkingSpotRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = ParkingControlApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class ParkingSpotRestIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired ParkingSpotRepository parkingSpotRepository;

    @AfterEach
    public void tearDownEach() {
        parkingSpotRepository.deleteAll();
    }

    @Test
    public void givenValidInput_whenCreateNewParkingSpot_thenCreateParkingSpot() throws Exception {
        Map<String, String> parkingSpotMap = Map.ofEntries(
            entry("parkingSpotNumber", "2058"),
            entry("licensePlateCar", "RRS8562"),
            entry("brandCar", "Audi"),
            entry("modelCar", "Q5"),
            entry("colorCar", "Black"),
            entry("responsibleName", "Test"),
            entry("apartment", "265"),
            entry("block", "8")
        );
        String requestJson = new ObjectMapper().writeValueAsString(parkingSpotMap);
        mvc.perform(post("/parking-spot").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        List<ParkingSpot> allParkingSpot = parkingSpotRepository.findAll();
        assertThat(allParkingSpot).extracting(ParkingSpot::getId).doesNotContainNull();
    }

    @Test
    public void givenInvalidInput_whenCreateNewParkingSpot_thenStatus400() throws Exception {
        Map<String, String> invalidParkingSpotMap = Map.ofEntries(
                entry("licensePlateCar", "RRS8562"),
                entry("brandCar", "Audi"),
                entry("modelCar", "Q5"),
                entry("colorCar", "Black"),
                entry("responsibleName", "Test"),
                entry("apartment", "265"),
                entry("block", "8")
        );
        String requestJson = new ObjectMapper().writeValueAsString(invalidParkingSpotMap);
        mvc.perform(post("/parking-spot").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
