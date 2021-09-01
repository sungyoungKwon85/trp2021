package com.kkwonsy.trp.service;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kkwonsy.trp.entity.City;
import com.kkwonsy.trp.exception.ErrorCode;
import com.kkwonsy.trp.exception.TrpException;
import com.kkwonsy.trp.model.CitySaveRequest;
import com.kkwonsy.trp.repository.CityRepository;
import com.kkwonsy.trp.repository.TripCityRepository;

import dto.CityResponseDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CityService {

    private final CityRepository cityRepository;
    private final MemberService memberService;
    private final TripCityRepository tripCityRepository;
    private final RedisService redisService;

    private static final int MAX_CITY_LIST_SIZE = 10;

    public City findCityOrThrow(Long cityId) throws TrpException {
        return cityRepository.findById(cityId)
            .orElseThrow(() -> new TrpException(ErrorCode.CITY_NOT_FOUND));
    }

    @Transactional
    public Long saveCity(CitySaveRequest request) throws TrpException {
        if (cityRepository.findByName(request.getName()).isPresent()) {
            throw new TrpException(ErrorCode.CITY_ALREADY_EXIST);
        }

        City newCity = cityRepository.save(City.builder().name(request.getName()).build());
        return newCity.getId();
    }

    public CityResponseDto getCityForMember(Long memberId, Long cityId) throws TrpException {
        City city = cityRepository.findById(cityId).orElseThrow(() -> new TrpException(ErrorCode.CITY_NOT_FOUND));
        redisService.putCityId(memberId, cityId);
        return CityResponseDto.buildFrom(city);
    }

    public List<CityResponseDto> getCities(Long memberId) throws TrpException {
        memberService.findMemberOrThrow(memberId);
        List<CityResponseDto> resultCities =
            tripCityRepository.findCitiesOnTrip(memberId, MAX_CITY_LIST_SIZE);
        if (isCityListFull(resultCities.size())) {
            return resultCities;
        }

        resultCities = unionList(
            resultCities,
            tripCityRepository.findCitiesForTripLater(memberId, getCityListRemainSize(resultCities.size()))
        );
        if (isCityListFull(resultCities.size())) {
            return resultCities;
        }

        LocalDateTime CREATED_TO = LocalDateTime.now();
        LocalDateTime CREATED_FROM = CREATED_TO.minusDays(1);
        resultCities = unionList(
            resultCities,
            cityRepository.findCitiesByCreatedPeriodAndIdNotIn(
                CREATED_FROM, CREATED_TO,
                getIdsBy(resultCities),
                PageRequest.of(0, getCityListRemainSize(resultCities.size())))
                .stream()
                .map(city -> CityResponseDto.buildFrom(city))
                .collect(Collectors.toList())
        );
        if (isCityListFull(resultCities.size())) {
            return resultCities;
        }

        List<Long> cachedCityIds = redisService.getCityIds(memberId, getIdsBy(resultCities), MAX_CITY_LIST_SIZE);
        List<City> cachedCities = cityRepository.findAllById(cachedCityIds);
        Collections.sort(cachedCities,
            Comparator.comparing(city -> cachedCityIds.indexOf(city.getId())));
        resultCities = unionList(
            resultCities,
            cachedCities.stream()
                .map(city -> CityResponseDto.buildFrom(city))
                .collect(Collectors.toList())
        );
        if (isCityListFull(resultCities.size())) {
            return resultCities;
        }

        return unionList(
            resultCities,
            getRandomCities(getIdsBy(resultCities), getCityListRemainSize(resultCities.size()))
        );
    }

    private List<CityResponseDto> getRandomCities(List<Long> notInIds, int size) {
        Field[] declaredFields = City.class.getDeclaredFields();
        int index = new Random().nextInt(declaredFields.length);
        final String fieldName = declaredFields[index].getName();

        Direction direction = index % 2 == 0 ? Direction.ASC : Direction.DESC;
        PageRequest pageRequest = PageRequest.of(0, size, Sort.by(direction, fieldName));

        return cityRepository.findCitiesByIdNotIn(notInIds, pageRequest).stream()
            .map(city -> CityResponseDto.buildFrom(city))
            .collect(Collectors.toList());
    }

    private List<Long> getIdsBy(List<CityResponseDto> resultCities) {
        return resultCities.stream().map(city -> city.getId()).collect(Collectors.toList());
    }

    private List<CityResponseDto> unionList(List<CityResponseDto> baseList, List<CityResponseDto> list) {
        baseList.addAll(list);
        return baseList;
    }

    private int getCityListRemainSize(int cumulativeSize) {
        return MAX_CITY_LIST_SIZE - cumulativeSize;
    }

    private boolean isCityListFull(int cumulativeSize) {
        return cumulativeSize == MAX_CITY_LIST_SIZE;
    }
}
