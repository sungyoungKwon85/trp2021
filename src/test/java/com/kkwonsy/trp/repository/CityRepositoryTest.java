package com.kkwonsy.trp.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.kkwonsy.trp.entity.City;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@DataJpaTest
class CityRepositoryTest {

    @Autowired
    private CityRepository cityRepository;

    @Test
    public void findByName() throws Exception {
        // given
        City seoul = City.builder().name("Seoul").build();
        cityRepository.save(seoul);

        // when
        City city = cityRepository.findByName(seoul.getName()).orElseGet(null);

        // then
        assertNotNull(city);
        assertEquals(city.getName(), seoul.getName());
    }
}