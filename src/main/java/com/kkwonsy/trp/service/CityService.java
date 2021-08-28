package com.kkwonsy.trp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kkwonsy.trp.entity.City;
import com.kkwonsy.trp.exception.ErrorCode;
import com.kkwonsy.trp.exception.TrpException;
import com.kkwonsy.trp.model.CitySaveRequest;
import com.kkwonsy.trp.repository.CityRepository;

import dto.CityResponseDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CityService {

    private final CityRepository cityRepository;

    public CityResponseDto getCity(Long cityId) throws TrpException {
        City city = cityRepository.findById(cityId).orElseThrow(() -> new TrpException(ErrorCode.CITY_NOT_FOUND));
        return CityResponseDto.builder()
            .id(city.getId())
            .name(city.getName())
            .createdAt(city.getCreatedAt())
            .lastModifiedAt(city.getLastModifiedAt())
            .build();
    }

    @Transactional
    public Long saveCity(CitySaveRequest request) throws TrpException {
        if (cityRepository.findByName(request.getName()).isPresent()) {
            throw new TrpException(ErrorCode.CITY_ALREADY_EXIST);
        }

        City newCity = cityRepository.save(City.builder().name(request.getName()).build());
        return newCity.getId();
    }
}
