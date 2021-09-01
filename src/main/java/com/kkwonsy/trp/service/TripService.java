package com.kkwonsy.trp.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kkwonsy.trp.entity.City;
import com.kkwonsy.trp.entity.Member;
import com.kkwonsy.trp.entity.Trip;
import com.kkwonsy.trp.exception.ErrorCode;
import com.kkwonsy.trp.exception.TrpException;
import com.kkwonsy.trp.model.TripSaveRequest;
import com.kkwonsy.trp.repository.TripRepository;

import dto.TripResponseDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TripService {

    private final TripRepository tripRepository;
    private final MemberService memberService;
    private final CityService cityService;

    public TripResponseDto getTrip(Long memberId, Long tripId) throws TrpException {
        memberService.findMemberOrThrow(memberId);

        Trip trip = tripRepository.findByIdAndMemberId(tripId, memberId)
            .orElseThrow(() -> new TrpException(ErrorCode.TRIP_NOT_FOUND));
        return getTripResponseDtoBuild(trip);
    }

    @Transactional
    public Long saveTrip(Long memberId, TripSaveRequest request) throws TrpException {
        validTripDate(request);

        Member member = memberService.findMemberOrThrow(memberId);
        City city = cityService.findCityOrThrow(request.getCityId());

        if (tripRepository.findByMemberIdAndCityId(memberId, request.getCityId()).isPresent()) {
            throw new TrpException(ErrorCode.TRIP_ALREADY_EXIST);
        }

        Trip newTrip = Trip.builder()
            .title(request.getTitle())
            .member(member)
            .city(city)
            .startAt(request.getStartAt())
            .endAt(request.getEndAt())
            .build();
        Trip saved = tripRepository.save(newTrip);
        return saved.getId();
    }

    public List<TripResponseDto> getTrips(Long memberId) {
        return tripRepository.findAllByMemberId(memberId).stream()
            .map(trip -> getTripResponseDtoBuild(trip))
            .collect(Collectors.toList());
    }

    private TripResponseDto getTripResponseDtoBuild(Trip trip) {
        return TripResponseDto.builder()
            .id(trip.getId())
            .title(trip.getTitle())
            .memberId(trip.getMember().getId())
            .cityId(trip.getCity().getId())
            .startAt(trip.getStartAt())
            .endAt(trip.getEndAt())
            .createdAt(trip.getCreatedAt())
            .lastModifiedAt(trip.getLastModifiedAt())
            .build();
    }

    private void validTripDate(TripSaveRequest request) throws TrpException {
        LocalDate startAt = request.getStartAt();
        LocalDate endAt = request.getEndAt();
        if (startAt.isAfter(endAt)
            || startAt.isBefore(LocalDate.now())
            || startAt.isEqual(LocalDate.now())
        ) {
            throw new TrpException(ErrorCode.TRIP_INVALID_DATE);
        }
    }
}
