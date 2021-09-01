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
import com.kkwonsy.trp.model.IdResponse;
import com.kkwonsy.trp.model.ListResponse;
import com.kkwonsy.trp.model.ObjectResponse;
import com.kkwonsy.trp.model.TripSaveRequest;
import com.kkwonsy.trp.service.TripService;

import dto.TripResponseDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1")
public class TripController {

    private final TripService tripService;

    @GetMapping(value = "/members/{memberId}/trips/{tripId}")
    public ResponseEntity getTrip(
        @PathVariable(name = "memberId") Long memberId, @PathVariable(name = "tripId") Long tripId
    ) throws TrpException {
        TripResponseDto trip = tripService.getTrip(memberId, tripId);
        return new ResponseEntity(new ObjectResponse<>(trip), HttpStatus.OK);
    }

    @GetMapping(value = "/members/{memberId}/trips")
    public ResponseEntity getTrips(@PathVariable(name = "memberId") Long memberId) {
        List<TripResponseDto> trips = tripService.getTrips(memberId);
        return new ResponseEntity(new ListResponse<>(trips), HttpStatus.OK);
    }

    @PostMapping(value = "/members/{memberId}/trips")
    public ResponseEntity saveTrip(
        @PathVariable(name = "memberId") Long memberId,
        @Valid @RequestBody TripSaveRequest request
    ) throws TrpException {
        Long savedId = tripService.saveTrip(memberId, request);
        return new ResponseEntity(new ObjectResponse(new IdResponse(savedId)), HttpStatus.OK);
    }
}
