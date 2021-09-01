package com.kkwonsy.trp.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.kkwonsy.trp.entity.City;

import dto.CityResponseDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class TripCityRepository {

    private final EntityManager em;

    public List<CityResponseDto> findCitiesOnTrip(Long memberId, int limit) {
        List<City> cities = em.createQuery(
            "select c "
                + "from trp_city c "
                + "left outer join fetch trp_trip t "
                + "on c.id = t.city.id "
                + "where t.member.id = :memberId "
                + "and (t.startAt <= :now and t.endAt >= :now)"
            , City.class)
            .setParameter("memberId", memberId)
            .setParameter("now", LocalDate.now())
            .setMaxResults(limit)
            .getResultList();
        return cities.stream()
            .map(city -> CityResponseDto.buildFrom(city))
            .collect(Collectors.toList());
    }

    public List<CityResponseDto> findCitiesForTripLater(Long memberId, int limit) {
        List<City> cities = em.createQuery(
            "select c "
                + "from trp_city c "
                + "left outer join fetch trp_trip t "
                + "on c.id = t.city.id "
                + "where t.member.id = :memberId "
                + "and t.startAt > :now "
                + "order by t.startAt asc "
            , City.class)
            .setParameter("memberId", memberId)
            .setParameter("now", LocalDate.now())
            .setMaxResults(limit)
            .getResultList();
        return cities.stream()
            .map(city -> CityResponseDto.buildFrom(city))
            .collect(Collectors.toList());
    }

}
