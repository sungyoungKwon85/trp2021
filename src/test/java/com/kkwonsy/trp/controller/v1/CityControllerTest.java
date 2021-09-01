package com.kkwonsy.trp.controller.v1;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.kkwonsy.trp.model.CitySaveRequest;
import com.kkwonsy.trp.service.CityService;
import com.kkwonsy.trp.util.JsonUtil;

@WebMvcTest(CityController.class)
class CityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CityService cityService;

    @Test
    public void getCity() throws Exception {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/members/{memberId}/cities/{cityId}"
                , Long.MAX_VALUE, Long.MAX_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getCities() throws Exception {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/members/{memberId}/cities", Long.MAX_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    public void saveCity() throws Exception {
        String content = JsonUtil.toJsonString(new CitySaveRequest("city1"));
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/cities")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

}