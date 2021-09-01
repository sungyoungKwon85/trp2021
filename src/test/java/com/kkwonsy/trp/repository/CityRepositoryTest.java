package com.kkwonsy.trp.repository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.kkwonsy.trp.config.JpaConfig;
import com.kkwonsy.trp.entity.City;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest(
    includeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = JpaConfig.class
    )
)
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

    @Test
    public void findRegisteredPeriodAndNotInIds() throws Exception {
        // given
        City seoul = City.builder().name("Seoul").build();
        City pusan = City.builder().name("Pusan").build();
        cityRepository.save(seoul);
        cityRepository.save(pusan);

        // when
        LocalDateTime now = LocalDateTime.now();
        List<City> result = cityRepository
            .findCitiesByCreatedPeriodAndIdNotIn(now.minusDays(1), now, Arrays.asList(seoul.getId()),
                PageRequest.of(0, 10));

        // then
        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getName(), pusan.getName());
    }

    @Test
    public void findRegisteredPeriodAndNotInIds_when_more_than_size() throws Exception {
        // given
        City seoul = City.builder().name("Seoul").build();
        City pusan = City.builder().name("Pusan").build();
        cityRepository.save(seoul);
        cityRepository.save(pusan);

        // when
        LocalDateTime now = LocalDateTime.now();
        List<City> result = cityRepository
            .findCitiesByCreatedPeriodAndIdNotIn(now.minusDays(1), now, Arrays.asList(), PageRequest.of(0, 1));

        // then
        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getName(), pusan.getName());
    }

    @Test
    public void findCitiesByIdNotIn() {
        City city1 = cityRepository.save(City.builder().name("city1").build());
        cityRepository.save(City.builder().name("city2").build());
        cityRepository.save(City.builder().name("city3").build());
        List<City> list = cityRepository.findCitiesByIdNotIn(
            Arrays.asList(city1.getId()), PageRequest.of(0, 50, Sort.by(Direction.ASC, "name")));
        assertNotNull(list);
        assertTrue(list.size() >= 2);
    }
}