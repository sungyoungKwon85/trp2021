package com.kkwonsy.trp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kkwonsy.trp.entity.Trip;

public interface TripRepository extends JpaRepository<Trip, Long> {

    Optional<Trip> findByIdAndMemberId(Long id, Long memberId);

    Optional<Trip> findByMemberIdAndCityId(Long memberId, Long cityId);

    List<Trip> findAllByMemberId(Long memberId);
}
