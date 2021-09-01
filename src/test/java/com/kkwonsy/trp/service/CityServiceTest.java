package com.kkwonsy.trp.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.kkwonsy.trp.entity.City;
import com.kkwonsy.trp.entity.Member;
import com.kkwonsy.trp.entity.Trip;
import com.kkwonsy.trp.repository.CityRepository;
import com.kkwonsy.trp.repository.MemberRepository;
import com.kkwonsy.trp.repository.TripCityRepository;
import com.kkwonsy.trp.repository.TripRepository;

import dto.CityResponseDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@Rollback
class CityServiceTest {

    @Autowired
    private CityService cityService;

    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private TripCityRepository tripCityRepository;
    @Autowired
    private RedisService redisService;
    @Autowired
    private EntityManager entityManager;

    @Test
    public void getCities_조회된도시없고_하루이내등록된도시만존재_랜덤없음() throws Exception {
        // given
        LocalDate now = LocalDate.now();

        Member member = memberRepository.save(Member.builder().name("member").build());

        // on trip
        City cityTrip1 = cityRepository.save(City.builder().name("cityTrip1").build());
        City cityTrip2 = cityRepository.save(City.builder().name("cityTrip2").build());

        // future trip
        City cityTripFuture1 = cityRepository.save(City.builder().name("cityTripFuture1").build());
        City cityTripFuture2 = cityRepository.save(City.builder().name("cityTripFuture2").build());

        City cityLatest4 = cityRepository.save(City.builder().name("cityLatest4").build());
        City cityLatest3 = cityRepository.save(City.builder().name("cityLatest3").build());
        City cityLatest2 = cityRepository.save(City.builder().name("cityLatest2").build());
        City cityLatest1 = cityRepository.save(City.builder().name("cityLatest1").build());

        Trip tripOn1 = tripRepository.save(Trip.builder()
            .member(member).city(cityTrip1).title("first")
            .startAt(now.minusDays(5)).endAt(now.plusDays(5)).build());
        Trip tripOn2 = tripRepository.save(Trip.builder()
            .member(member).city(cityTrip2).title("second")
            .startAt(now.minusDays(4)).endAt(now.plusDays(4)).build());
        Trip tripFuture1 = tripRepository.save(Trip.builder()
            .member(member).city(cityTripFuture1).title("3rd")
            .startAt(now.plusDays(1)).endAt(now.plusDays(4)).build());
        Trip tripFuture2 = tripRepository.save(Trip.builder()
            .member(member).city(cityTripFuture2).title("4th")
            .startAt(now.plusDays(2)).endAt(now.plusDays(7)).build());

        // when
        List<CityResponseDto> cities = cityService.getCities(member.getId());

        // then
        assertNotNull(cities);
        assertEquals(cities.size(), 8);
        assertEquals(cities.get(0).getName(), cityTrip1.getName());
        assertEquals(cities.get(1).getName(), cityTrip2.getName());
        assertEquals(cities.get(2).getName(), cityTripFuture1.getName());
        assertEquals(cities.get(3).getName(), cityTripFuture2.getName());
        assertEquals(cities.get(4).getName(), cityLatest1.getName());
        assertEquals(cities.get(5).getName(), cityLatest2.getName());
        assertEquals(cities.get(6).getName(), cityLatest3.getName());
        assertEquals(cities.get(7).getName(), cityLatest4.getName());
    }

    @Test
    public void getCities_1일이내등록도시없고_조회된도시존재_나머지랜덤() throws Exception {
        // given
        LocalDate now = LocalDate.now();

        Member member = memberRepository.save(Member.builder().name("member").build());

        // on trip
        City cityTrip1 = cityRepository.save(City.builder().name("cityTrip1").build());
        City cityTrip2 = cityRepository.save(City.builder().name("cityTrip2").build());

        // future trip
        City cityTripFuture1 = cityRepository.save(City.builder().name("cityTripFuture1").build());
        City cityTripFuture2 = cityRepository.save(City.builder().name("cityTripFuture2").build());

        // cached
        City cityCached1 = cityRepository.save(City.builder().name("cityCached1").build());
        City cityCached2 = cityRepository.save(City.builder().name("cityCached2").build());

        // random
        City cityLatest2 = cityRepository.save(City.builder().name("cityLatest2").build());
        City cityLatest1 = cityRepository.save(City.builder().name("cityLatest1").build());
        modifyCreatedAt(LocalDateTime.now().minusDays(1), cityLatest1.getId());
        modifyCreatedAt(LocalDateTime.now().minusDays(1), cityLatest2.getId());
        modifyCreatedAt(LocalDateTime.now().minusDays(1), cityCached1.getId());
        modifyCreatedAt(LocalDateTime.now().minusDays(1), cityCached2.getId());

        cityService.getCityForMember(member.getId(), cityTrip1.getId());
        cityService.getCityForMember(member.getId(), cityCached1.getId());
        cityService.getCityForMember(member.getId(), cityCached2.getId());

        Trip tripOn1 = tripRepository.save(Trip.builder()
            .member(member).city(cityTrip1).title("first")
            .startAt(now.minusDays(5)).endAt(now.plusDays(5)).build());
        Trip tripOn2 = tripRepository.save(Trip.builder()
            .member(member).city(cityTrip2).title("second")
            .startAt(now.minusDays(4)).endAt(now.plusDays(4)).build());
        Trip tripFuture1 = tripRepository.save(Trip.builder()
            .member(member).city(cityTripFuture1).title("3rd")
            .startAt(now.plusDays(1)).endAt(now.plusDays(4)).build());
        Trip tripFuture2 = tripRepository.save(Trip.builder()
            .member(member).city(cityTripFuture2).title("4th")
            .startAt(now.plusDays(2)).endAt(now.plusDays(7)).build());

        // when
        List<CityResponseDto> cities = cityService.getCities(member.getId());

        // then
        assertNotNull(cities);
        assertEquals(cities.size(), 8);
        assertEquals(cities.get(0).getName(), cityTrip1.getName());
        assertEquals(cities.get(1).getName(), cityTrip2.getName());
        assertEquals(cities.get(2).getName(), cityTripFuture1.getName());
        assertEquals(cities.get(3).getName(), cityTripFuture2.getName());
        assertEquals(cities.get(4).getName(), cityCached2.getName());
        assertEquals(cities.get(5).getName(), cityCached1.getName());
        assertTrue(cities.get(6).getName() == cityLatest1.getName()
            || cities.get(6).getName() == cityLatest2.getName());
        assertTrue(cities.get(7).getName() == cityLatest1.getName()
            || cities.get(7).getName() == cityLatest2.getName());
    }

    @Test
    public void getCities_1일이내등록도시없고_캐싱없고_모두랜덤() throws Exception {
        // given
        LocalDate now = LocalDate.now();

        Member member = memberRepository.save(Member.builder().name("member").build());

        // on trip
        City cityTrip1 = cityRepository.save(City.builder().name("cityTrip1").build());
        City cityTrip2 = cityRepository.save(City.builder().name("cityTrip2").build());

        // future trip
        City cityTripFuture1 = cityRepository.save(City.builder().name("cityTripFuture1").build());
        City cityTripFuture2 = cityRepository.save(City.builder().name("cityTripFuture2").build());

        // random
        City cityLatest4 = cityRepository.save(City.builder().name("cityLatest4").build());
        City cityLatest3 = cityRepository.save(City.builder().name("cityLatest3").build());
        City cityLatest2 = cityRepository.save(City.builder().name("cityLatest2").build());
        City cityLatest1 = cityRepository.save(City.builder().name("cityLatest1").build());
        modifyCreatedAt(LocalDateTime.now().minusDays(1), cityLatest1.getId());
        modifyCreatedAt(LocalDateTime.now().minusDays(1), cityLatest2.getId());
        modifyCreatedAt(LocalDateTime.now().minusDays(1), cityLatest4.getId());
        modifyCreatedAt(LocalDateTime.now().minusDays(1), cityLatest3.getId());

        Trip tripOn1 = tripRepository.save(Trip.builder()
            .member(member).city(cityTrip1).title("first")
            .startAt(now.minusDays(5)).endAt(now.plusDays(5)).build());
        Trip tripOn2 = tripRepository.save(Trip.builder()
            .member(member).city(cityTrip2).title("second")
            .startAt(now.minusDays(4)).endAt(now.plusDays(4)).build());
        Trip tripFuture1 = tripRepository.save(Trip.builder()
            .member(member).city(cityTripFuture1).title("3rd")
            .startAt(now.plusDays(1)).endAt(now.plusDays(4)).build());
        Trip tripFuture2 = tripRepository.save(Trip.builder()
            .member(member).city(cityTripFuture2).title("4th")
            .startAt(now.plusDays(2)).endAt(now.plusDays(7)).build());

        // when
        List<CityResponseDto> cities = cityService.getCities(member.getId());

        // then
        assertNotNull(cities);
        assertEquals(cities.size(), 8);
        assertEquals(cities.get(0).getName(), cityTrip1.getName());
        assertEquals(cities.get(1).getName(), cityTrip2.getName());
        assertEquals(cities.get(2).getName(), cityTripFuture1.getName());
        assertEquals(cities.get(3).getName(), cityTripFuture2.getName());
        assertTrue(cities.get(4).getName() == cityLatest1.getName()
            || cities.get(4).getName() == cityLatest2.getName()
            || cities.get(4).getName() == cityLatest3.getName()
            || cities.get(4).getName() == cityLatest4.getName());
        assertTrue(cities.get(5).getName() == cityLatest1.getName()
            || cities.get(5).getName() == cityLatest2.getName()
            || cities.get(5).getName() == cityLatest3.getName()
            || cities.get(5).getName() == cityLatest4.getName());
        assertTrue(cities.get(6).getName() == cityLatest1.getName()
            || cities.get(6).getName() == cityLatest2.getName()
            || cities.get(6).getName() == cityLatest3.getName()
            || cities.get(6).getName() == cityLatest4.getName());
        assertTrue(cities.get(7).getName() == cityLatest1.getName()
            || cities.get(7).getName() == cityLatest2.getName()
            || cities.get(7).getName() == cityLatest3.getName()
            || cities.get(7).getName() == cityLatest4.getName());
    }

    private void modifyCreatedAt(LocalDateTime dateTime, Long id) {
        entityManager.createQuery("update trp_city c set c.createdAt = :aWeekAgo where c.id = :id")
            .setParameter("aWeekAgo", dateTime)
            .setParameter("id", id)
            .executeUpdate();
    }
}