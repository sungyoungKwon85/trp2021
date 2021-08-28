package com.kkwonsy.trp.service;

import java.lang.reflect.Field;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kkwonsy.trp.entity.City;
import com.kkwonsy.trp.exception.TrpException;
import com.kkwonsy.trp.model.CitySaveRequest;
import com.kkwonsy.trp.repository.CityRepository;

import dto.CityResponseDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CityServiceUnitTest {

    @InjectMocks
    private CityService cityService;

    @Mock
    private CityRepository cityRepository;

    private City seoul;

    @BeforeEach
    public void beforeEach() {
        seoul = City.builder().name("Seoul").build();
    }

    @Test
    public void getCity() throws Exception {
        // given
        when(cityRepository.findById(1L)).thenReturn(Optional.of(seoul));

        // when
        CityResponseDto city = cityService.getCity(1L);

        // then
        assertNotNull(city);
        assertEquals(city.getName(), seoul.getName());
    }

    @Test
    public void getCity_not_found() throws Exception {
        // given
        when(cityRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        assertThrows(TrpException.class, () -> cityService.getCity(1L));
    }

    @Test
    public void saveCity_name_already_exist() throws Exception {
        // given
        CitySaveRequest request = new CitySaveRequest(seoul.getName());
        when(cityRepository.findByName(request.getName())).thenReturn(Optional.of(seoul));

        // when
        assertThrows(TrpException.class, () -> cityService.saveCity(request));
    }

    @Test
    public void saveCity() throws Exception {
        // given
        CitySaveRequest request = new CitySaveRequest("Pusan");
        when(cityRepository.findByName(request.getName())).thenReturn(Optional.empty());

        City pusan = City.builder().name(request.getName()).build();
        Field idField = pusan.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        long id = 1L;
        idField.set(pusan, id);
        when(cityRepository.save(any(City.class))).thenReturn(pusan);

        // when
        Long saveCity = cityService.saveCity(request);

        // then
        assertNotNull(saveCity);
        assertEquals(saveCity, id);
    }


}