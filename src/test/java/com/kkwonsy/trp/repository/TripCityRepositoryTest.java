package com.kkwonsy.trp.repository;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.kkwonsy.trp.entity.City;
import com.kkwonsy.trp.entity.Member;
import com.kkwonsy.trp.entity.Trip;

import dto.CityResponseDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
@Rollback
class TripCityRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private TripCityRepository tripCityRepository;

    private Member kwon;
    private City seoul;
    private Trip kwonSeoulNow;

    @BeforeEach
    public void beforeEach() {
        kwon = memberRepository.save(Member.builder().name("kwon").build());
        seoul = cityRepository.save(City.builder().name("Seoul").build());

        LocalDate now = LocalDate.now();
        kwonSeoulNow = Trip.builder()
            .title("My first Seoul!")
            .member(kwon)
            .city(seoul)
            .startAt(now.minusDays(1))
            .endAt(now.plusDays(10))
            .build();
        tripRepository.save(kwonSeoulNow);
    }

    @Test
    public void findCurrentTripCities() throws Exception {
        List<CityResponseDto> currentTripCities = tripCityRepository.findCitiesOnTrip(kwon.getId(), 10);

        assertNotNull(currentTripCities);
        assertEquals(currentTripCities.size(), 1);
        assertEquals(currentTripCities.get(0).getName(), seoul.getName());
    }

}