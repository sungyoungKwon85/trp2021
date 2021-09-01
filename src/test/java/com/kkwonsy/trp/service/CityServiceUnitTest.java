package com.kkwonsy.trp.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kkwonsy.trp.entity.City;
import com.kkwonsy.trp.entity.Member;
import com.kkwonsy.trp.exception.TrpException;
import com.kkwonsy.trp.model.CitySaveRequest;
import com.kkwonsy.trp.repository.CityRepository;
import com.kkwonsy.trp.repository.TripCityRepository;

import dto.CityResponseDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CityServiceUnitTest {

    @InjectMocks
    private CityService cityService;

    @Mock
    private MemberService memberService;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private TripCityRepository tripCityRepository;

    @Mock
    private RedisService redisService;

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
        CityResponseDto city = cityService.getCityForMember(1L, 1L);

        // then
        assertNotNull(city);
        assertEquals(city.getName(), seoul.getName());
    }

    @Test
    public void getCity_not_found() throws Exception {
        // given
        when(cityRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        assertThrows(TrpException.class, () -> cityService.getCityForMember(1L, 1L));
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

    @Test
    public void getCities_when_여행중인도시가_10개() throws Exception {
        // given
        Member kwon = getKwon();
        when(memberService.findMemberOrThrow(anyLong())).thenReturn(kwon);

        final int LIST_SIZE = 10;
        List<CityResponseDto> resultCities = new ArrayList<>();
        for (int i = 0; i < LIST_SIZE; i++) {
            CityResponseDto city = CityResponseDto.builder()
                .id((long) i)
                .name("Seoul" + i)
                .build();
            resultCities.add(city);
        }
        when(tripCityRepository.findCitiesOnTrip(anyLong(), anyInt())).thenReturn(resultCities);

        // when
        List<CityResponseDto> cities = cityService.getCities(1L);

        // then
        assertNotNull(cities);
        assertEquals(cities.size(), LIST_SIZE);
        verify(tripCityRepository, Mockito.times(1))
            .findCitiesOnTrip(anyLong(), anyInt());
        verify(tripCityRepository, Mockito.times(0))
            .findCitiesForTripLater(anyLong(), anyInt());
        verify(cityRepository, Mockito.times(0))
            .findCitiesByCreatedPeriodAndIdNotIn(any(), any(), any(), any());
        verify(redisService, Mockito.times(0))
            .getCityIds(anyLong(), any(), anyInt());
        verify(cityRepository, Mockito.times(0))
            .findAllById(any());
        verify(cityRepository, Mockito.times(0))
            .findCitiesByIdNotIn(any(), any());
    }

    @Test
    public void getCities_when_여행중인도시5_시작안된여행5() throws Exception {
        // given
        Member kwon = getKwon();
        when(memberService.findMemberOrThrow(anyLong())).thenReturn(kwon);

        final int LIST_SIZE = 10;
        List<CityResponseDto> resultCities = new ArrayList<>();
        List<CityResponseDto> futureCities = new ArrayList<>();
        for (int i = 0; i < LIST_SIZE; i++) {
            CityResponseDto city = CityResponseDto.builder()
                .id((long) i)
                .name("Seoul" + i)
                .build();
            if (i < 5) {
                resultCities.add(city);
            } else {
                futureCities.add(city);
            }

        }
        when(tripCityRepository.findCitiesOnTrip(anyLong(), anyInt())).thenReturn(resultCities);
        when(tripCityRepository.findCitiesForTripLater(anyLong(), anyInt())).thenReturn(futureCities);

        // when
        List<CityResponseDto> cities = cityService.getCities(1L);

        // then
        assertNotNull(cities);
        assertEquals(cities.size(), LIST_SIZE);
        verify(tripCityRepository, Mockito.times(1))
            .findCitiesOnTrip(anyLong(), anyInt());
        verify(tripCityRepository, Mockito.times(1))
            .findCitiesForTripLater(anyLong(), anyInt());
        verify(cityRepository, Mockito.times(0))
            .findCitiesByCreatedPeriodAndIdNotIn(any(), any(), any(), any());
        verify(redisService, Mockito.times(0))
            .getCityIds(anyLong(), any(), anyInt());
        verify(cityRepository, Mockito.times(0))
            .findAllById(any());
        verify(cityRepository, Mockito.times(0))
            .findCitiesByIdNotIn(any(), any());
    }

    @Test
    public void getCities_when_여행중인도시3_시작안된여행3_최근1일이내등록도시4() throws Exception {
        // given
        Member kwon = getKwon();
        when(memberService.findMemberOrThrow(anyLong())).thenReturn(kwon);

        List<CityResponseDto> resultCities = new ArrayList<>();
        List<CityResponseDto> futureCities = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            CityResponseDto city = CityResponseDto.builder()
                .id((long) i)
                .name("Seoul" + i)
                .build();
            if (i < 3) {
                resultCities.add(city);
            } else {
                futureCities.add(city);
            }

        }
        List<City> recendRegisteredCities = new ArrayList<>();
        for (int i = 6; i < 10; i++) {
            recendRegisteredCities.add(City.builder().name("Pusan" + i).build());
        }
        when(tripCityRepository.findCitiesOnTrip(anyLong(), anyInt())).thenReturn(resultCities);
        when(tripCityRepository.findCitiesForTripLater(anyLong(), anyInt())).thenReturn(futureCities);
        when(cityRepository.findCitiesByCreatedPeriodAndIdNotIn(any(), any(), any(), any()))
            .thenReturn(recendRegisteredCities);

        // when
        cityService.getCities(1L);

        // then
        verify(tripCityRepository, Mockito.times(1))
            .findCitiesOnTrip(anyLong(), anyInt());
        verify(tripCityRepository, Mockito.times(1))
            .findCitiesForTripLater(anyLong(), anyInt());
        verify(cityRepository, Mockito.times(1))
            .findCitiesByCreatedPeriodAndIdNotIn(any(), any(), any(), any());
        verify(redisService, Mockito.times(0))
            .getCityIds(anyLong(), any(), anyInt());
        verify(cityRepository, Mockito.times(0))
            .findAllById(any());
        verify(cityRepository, Mockito.times(0))
            .findCitiesByIdNotIn(any(), any());
    }

    @Test
    public void getCities_when_여행중인도시3_시작안된여행3_최근1일이내등록도시2_최근1주일이내조회2() throws Exception {
        // given
        Member kwon = getKwon();
        when(memberService.findMemberOrThrow(anyLong())).thenReturn(kwon);

        List<CityResponseDto> resultCities = new ArrayList<>();
        List<CityResponseDto> futureCities = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            CityResponseDto city = CityResponseDto.builder()
                .id((long) i)
                .name("Seoul" + i)
                .build();
            if (i < 3) {
                resultCities.add(city);
            } else {
                futureCities.add(city);
            }

        }
        List<City> recendRegisteredCities = new ArrayList<>();
        for (int i = 6; i < 8; i++) {
            recendRegisteredCities.add(City.builder().name("Pusan" + i).build());
        }
        List<Long> cachedIds = new ArrayList<>();
        List<City> cachedCities = new ArrayList<>();
        for (int i = 8; i < 10; i++) {
            cachedIds.add((long) i);
            cachedCities.add(City.builder().name("Pusan" + i).build());
        }
        when(tripCityRepository.findCitiesOnTrip(anyLong(), anyInt())).thenReturn(resultCities);
        when(tripCityRepository.findCitiesForTripLater(anyLong(), anyInt())).thenReturn(futureCities);
        when(cityRepository.findCitiesByCreatedPeriodAndIdNotIn(any(), any(), any(), any()))
            .thenReturn(recendRegisteredCities);
        when(redisService.getCityIds(anyLong(), any(), anyInt())).thenReturn(cachedIds);
        when(cityRepository.findAllById(any())).thenReturn(cachedCities);

        // when
        cityService.getCities(1L);

        // then
        verify(tripCityRepository, Mockito.times(1))
            .findCitiesOnTrip(anyLong(), anyInt());
        verify(tripCityRepository, Mockito.times(1))
            .findCitiesForTripLater(anyLong(), anyInt());
        verify(cityRepository, Mockito.times(1))
            .findCitiesByCreatedPeriodAndIdNotIn(any(), any(), any(), any());
        verify(redisService, Mockito.times(1))
            .getCityIds(anyLong(), any(), anyInt());
        verify(cityRepository, Mockito.times(1))
            .findAllById(any());
        verify(cityRepository, Mockito.times(0))
            .findCitiesByIdNotIn(any(), any());
    }

    @Test
    public void getCities_when_여행중인도시2_시작안된여행2_최근1일이내등록도시2_최근1주일이내조회2_랜덤2() throws Exception {
        // given
        Member kwon = getKwon();
        when(memberService.findMemberOrThrow(anyLong())).thenReturn(kwon);

        List<CityResponseDto> resultCities = new ArrayList<>();
        List<CityResponseDto> futureCities = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            CityResponseDto city = CityResponseDto.builder()
                .id((long) i)
                .name("Seoul" + i)
                .build();
            if (i < 2) {
                resultCities.add(city);
            } else {
                futureCities.add(city);
            }

        }
        List<City> recendRegisteredCities = new ArrayList<>();
        for (int i = 4; i < 6; i++) {
            recendRegisteredCities.add(City.builder().name("Pusan" + i).build());
        }
        List<Long> cachedIds = new ArrayList<>();
        List<City> cachedCities = new ArrayList<>();
        for (int i = 6; i < 8; i++) {
            cachedIds.add((long) i);
            cachedCities.add(City.builder().name("Pusan" + i).build());
        }
        List<City> randomCities = new ArrayList<>();
        for (int i = 8; i < 10; i++) {
            randomCities.add(City.builder().name("Pusan" + i).build());
        }
        when(tripCityRepository.findCitiesOnTrip(anyLong(), anyInt())).thenReturn(resultCities);
        when(tripCityRepository.findCitiesForTripLater(anyLong(), anyInt())).thenReturn(futureCities);
        when(cityRepository.findCitiesByCreatedPeriodAndIdNotIn(any(), any(), any(), any()))
            .thenReturn(recendRegisteredCities);
        when(redisService.getCityIds(anyLong(), any(), anyInt())).thenReturn(cachedIds);
        when(cityRepository.findAllById(any())).thenReturn(cachedCities);
        when(cityRepository.findCitiesByIdNotIn(any(), any())).thenReturn(randomCities);

        // when
        cityService.getCities(1L);

        // then
        verify(tripCityRepository, Mockito.times(1))
            .findCitiesOnTrip(anyLong(), anyInt());
        verify(tripCityRepository, Mockito.times(1))
            .findCitiesForTripLater(anyLong(), anyInt());
        verify(cityRepository, Mockito.times(1))
            .findCitiesByCreatedPeriodAndIdNotIn(any(), any(), any(), any());
        verify(redisService, Mockito.times(1))
            .getCityIds(anyLong(), any(), anyInt());
        verify(cityRepository, Mockito.times(1))
            .findAllById(any());
        verify(cityRepository, Mockito.times(1))
            .findCitiesByIdNotIn(any(), any());
    }

    @Test
    public void getCities_when_여행중인도시0_시작안된여행4_최근1일이내등록도시2_최근1주일이내조회2_랜덤2() throws Exception {
        // given
        Member kwon = getKwon();
        when(memberService.findMemberOrThrow(anyLong())).thenReturn(kwon);

        List<CityResponseDto> resultCities = new ArrayList<>();
        List<CityResponseDto> futureCities = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            CityResponseDto city = CityResponseDto.builder()
                .id((long) i)
                .name("Seoul" + i)
                .build();
            futureCities.add(city);
        }
        List<City> recentRegisteredCities = new ArrayList<>();
        for (int i = 4; i < 6; i++) {
            recentRegisteredCities.add(City.builder().name("Pusan" + i).build());
        }
        List<Long> cachedIds = new ArrayList<>();
        List<City> cachedCities = new ArrayList<>();
        for (int i = 6; i < 8; i++) {
            cachedIds.add((long) i);
            cachedCities.add(City.builder().name("Pusan" + i).build());
        }
        List<City> randomCities = new ArrayList<>();
        for (int i = 8; i < 10; i++) {
            randomCities.add(City.builder().name("Pusan" + i).build());
        }
        when(tripCityRepository.findCitiesOnTrip(anyLong(), anyInt())).thenReturn(resultCities);
        when(tripCityRepository.findCitiesForTripLater(anyLong(), anyInt())).thenReturn(futureCities);
        when(cityRepository.findCitiesByCreatedPeriodAndIdNotIn(any(), any(), any(), any()))
            .thenReturn(recentRegisteredCities);
        when(redisService.getCityIds(anyLong(), any(), anyInt())).thenReturn(cachedIds);
        when(cityRepository.findAllById(any())).thenReturn(cachedCities);
        when(cityRepository.findCitiesByIdNotIn(any(), any())).thenReturn(randomCities);

        // when
        cityService.getCities(1L);

        // then
        verify(tripCityRepository, Mockito.times(1))
            .findCitiesOnTrip(anyLong(), anyInt());
        verify(tripCityRepository, Mockito.times(1))
            .findCitiesForTripLater(anyLong(), anyInt());
        verify(cityRepository, Mockito.times(1))
            .findCitiesByCreatedPeriodAndIdNotIn(any(), any(), any(), any());
        verify(redisService, Mockito.times(1))
            .getCityIds(anyLong(), any(), anyInt());
        verify(cityRepository, Mockito.times(1))
            .findAllById(any());
        verify(cityRepository, Mockito.times(1))
            .findCitiesByIdNotIn(any(), any());
    }

    private Member getKwon() {
        return Member.builder().name("kwon").build();
    }

}