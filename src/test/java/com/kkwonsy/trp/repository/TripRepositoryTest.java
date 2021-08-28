package com.kkwonsy.trp.repository;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import com.kkwonsy.trp.entity.City;
import com.kkwonsy.trp.entity.Member;
import com.kkwonsy.trp.entity.Trip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class TripRepositoryTest {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CityRepository cityRepository;

    private Member kwon;
    private City seoul;
    private Trip kwonSeoul;

    @BeforeEach
    public void beforeEach() {
        kwon = memberRepository.save(Member.builder().name("kwon").build());
        seoul = cityRepository.save(City.builder().name("Seoul").build());

        LocalDate now = LocalDate.now();
        kwonSeoul = Trip.builder()
            .title("My first Seoul!")
            .member(kwon)
            .city(seoul)
            .startAt(now.plusDays(4))
            .endAt(now.plusDays(10))
            .build();
    }

    @Test
    public void test_relation_success() throws Exception {
        // when
        Trip savedTrip = tripRepository.save(kwonSeoul);
        Trip foundTrip = tripRepository.findById(savedTrip.getId()).orElseGet(null);

        // then
        assertNotNull(foundTrip);
        assertEquals(foundTrip.getCity().getName(), seoul.getName());
    }

    @Test
    public void test_relation_fail_when_save_twice() throws Exception {
        tripRepository.save(kwonSeoul);

        LocalDate now = LocalDate.now();
        Trip secondSeoul = Trip.builder()
            .title("My second Seoul!")
            .member(kwon)
            .city(seoul)
            .startAt(now.plusMonths(1))
            .endAt(now.plusMonths(1).plusDays(7))
            .build();

        assertThrows(DataIntegrityViolationException.class, () -> tripRepository.save(secondSeoul));
    }

    @Test
    public void findByIdAndMemberId() throws Exception {
        // given
        Trip savedTrip = tripRepository.save(kwonSeoul);

        // when
        Trip trip = tripRepository.findByIdAndMemberId(savedTrip.getId(), kwon.getId()).orElseGet(null);

        // then
        assertNotNull(trip);
        assertEquals(trip.getTitle(), kwonSeoul.getTitle());
    }

    @Test
    public void findByMemberIdAndCityId() throws Exception {
        // given
        tripRepository.save(kwonSeoul);

        // when
        Trip trip = tripRepository.findByMemberIdAndCityId(kwon.getId(), seoul.getId()).orElseGet(null);

        // then
        assertNotNull(trip);
        assertEquals(trip.getTitle(), kwonSeoul.getTitle());
    }

    @Test
    public void findAllByMemberId() throws Exception {
        // given
        tripRepository.save(kwonSeoul);

        // when
        List<Trip> trips = tripRepository.findAllByMemberId(kwon.getId());

        // then
        assertNotNull(trips);
        assertEquals(trips.size(), 1);
        assertEquals(trips.get(0).getTitle(), kwonSeoul.getTitle());
    }
}