package com.kkwonsy.trp.service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kkwonsy.trp.entity.City;
import com.kkwonsy.trp.entity.Member;
import com.kkwonsy.trp.entity.Trip;
import com.kkwonsy.trp.exception.ErrorCode;
import com.kkwonsy.trp.exception.TrpException;
import com.kkwonsy.trp.model.TripSaveRequest;
import com.kkwonsy.trp.repository.TripRepository;

import dto.TripResponseDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TripServiceUnitTest {

    @InjectMocks
    private TripService tripService;

    @Mock
    private MemberService memberService;
    @Mock
    private CityService cityService;
    @Mock
    private TripRepository tripRepository;
//    @Mock
//    private MemberRepository memberRepository;
//    @Mock
//    private CityRepository cityRepository;

    private Member kwon;
    private City seoul;
    private TripSaveRequest request;
    private Trip kwonSeoul;

    @BeforeEach
    public void beforeEach() {
        kwon = Member.builder().name("kwon").build();
        seoul = City.builder().name("Seoul").build();
        request = TripSaveRequest.builder()
            .title("kwonSeoul")
            .cityId(2L)
            .startAt(LocalDate.now().plusDays(2))
            .endAt(LocalDate.now().plusDays(5))
            .build();
        kwonSeoul = Trip.builder()
            .title(request.getTitle())
            .member(kwon)
            .city(seoul)
            .startAt(request.getStartAt())
            .endAt(request.getEndAt())
            .build();
    }

    @Test
    public void getTrip() throws Exception {
        // given
        when(tripRepository.findByIdAndMemberId(anyLong(), anyLong())).thenReturn(Optional.of(kwonSeoul));

        // when
        TripResponseDto trip = tripService.getTrip(1L, 1L);

        // then
        assertNotNull(trip);
        assertEquals(trip.getTitle(), kwonSeoul.getTitle());
        assertEquals(trip.getStartAt(), kwonSeoul.getStartAt());
        assertEquals(trip.getEndAt(), kwonSeoul.getEndAt());
    }

    @Test
    public void getTrip_when_trip_not_found() throws Exception {
        // given
        when(tripRepository.findByIdAndMemberId(anyLong(), anyLong())).thenReturn(Optional.empty());

        // when
        TrpException trpException = assertThrows(TrpException.class, () -> tripService.getTrip(1L, 1L));
        assertEquals(trpException.getErrorCode(), ErrorCode.TRIP_NOT_FOUND);
    }

    @Test
    public void saveTrip() throws Exception {
        // given
        when(memberService.findMemberOrThrow(anyLong())).thenReturn(kwon);
        when(cityService.findCityOrThrow(anyLong())).thenReturn(seoul);
        when(tripRepository.findByMemberIdAndCityId(anyLong(), anyLong())).thenReturn(Optional.empty());

        long id = setId(kwonSeoul);
        when(tripRepository.save(any(Trip.class))).thenReturn(kwonSeoul);

        // when
        Long savedTrip = tripService.saveTrip(1L, request);

        // then
        assertNotNull(savedTrip);
        assertEquals(savedTrip, id);
    }

    @Test
    public void saveTrip_when_member_city_already_found() throws Exception {
        // given
        when(memberService.findMemberOrThrow(anyLong())).thenReturn(kwon);
        when(cityService.findCityOrThrow(anyLong())).thenReturn(seoul);
        when(tripRepository.findByMemberIdAndCityId(anyLong(), anyLong())).thenReturn(Optional.of(kwonSeoul));

        // when
        TrpException trpException = assertThrows(TrpException.class, () -> tripService.saveTrip(1L, request));
        assertEquals(trpException.getErrorCode(), ErrorCode.TRIP_ALREADY_EXIST);
    }

    private long setId(Trip kwonSeoul) throws NoSuchFieldException, IllegalAccessException {
        Field idField = kwonSeoul.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        long id = 1L;
        idField.set(kwonSeoul, id);
        return id;
    }

}