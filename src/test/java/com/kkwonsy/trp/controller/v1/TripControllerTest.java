package com.kkwonsy.trp.controller.v1;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.kkwonsy.trp.model.TripSaveRequest;
import com.kkwonsy.trp.service.TripService;
import com.kkwonsy.trp.util.JsonUtil;

@WebMvcTest(TripController.class)
class TripControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TripService tripService;

    @Test
    public void getTrip() throws Exception {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/members/{memberId}/trips/{tripId}"
                , Long.MAX_VALUE, Long.MAX_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getTrips() throws Exception {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/members/{memberId}/trips", Long.MAX_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void saveTrip() throws Exception {
        TripSaveRequest trip = TripSaveRequest.builder()
            .title("title")
            .cityId(Long.MAX_VALUE)
            .startAt(LocalDate.now().plusDays(4))
            .endAt(LocalDate.now().plusDays(10))
            .build();
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/members/{memberId}/trips", Long.MAX_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJsonString(trip))
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

}