package com.kkwonsy.trp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kkwonsy.trp.entity.City;

public interface CityRepository extends JpaRepository<City, Long> {

    Optional<City> findByName(String name);

    @Query("select c from trp_city c "
        + "where c.createdAt >= :start and c.createdAt <= :end "
        + "and c.id not in (:ids) "
        + "order by c.createdAt desc ")
    List<City> findCitiesByCreatedPeriodAndIdNotIn(LocalDateTime start, LocalDateTime end, List<Long> ids,
        Pageable pageable);

    List<City> findCitiesByIdNotIn(List<Long> ids, Pageable pageable);
}
