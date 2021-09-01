package com.kkwonsy.trp.controller.v1;

import java.util.List;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kkwonsy.trp.exception.TrpException;
import com.kkwonsy.trp.model.CitySaveRequest;
import com.kkwonsy.trp.model.IdResponse;
import com.kkwonsy.trp.model.ListResponse;
import com.kkwonsy.trp.model.ObjectResponse;
import com.kkwonsy.trp.service.CityService;

import dto.CityResponseDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1")
public class CityController {

    private final CityService cityService;

    @GetMapping(value = "/members/{memberId}/cities/{cityId}")
    public ResponseEntity getCity(@PathVariable(name = "memberId") Long memberId, @PathVariable Long cityId)
        throws TrpException {
        CityResponseDto city = cityService.getCityForMember(memberId, cityId);
        return new ResponseEntity(new ObjectResponse(city), HttpStatus.OK);
    }

    @GetMapping(value = "/members/{memberId}/cities")
    public ResponseEntity getCities(@PathVariable(name = "memberId") Long memberId) throws TrpException {
        List<CityResponseDto> cities = cityService.getCities(memberId);
        return new ResponseEntity(new ListResponse<>(cities), HttpStatus.OK);
    }

    @PostMapping(value = "/cities")
    public ResponseEntity saveCity(@Valid @RequestBody CitySaveRequest request) throws TrpException {
        Long savedId = cityService.saveCity(request);
        return new ResponseEntity(new ObjectResponse(new IdResponse(savedId)), HttpStatus.OK);
    }
}
