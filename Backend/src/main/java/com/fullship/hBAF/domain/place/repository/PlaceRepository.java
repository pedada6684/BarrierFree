package com.fullship.hBAF.domain.place.repository;

import com.fullship.hBAF.domain.place.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    public Place findPlaceByPoiId(String PoiId);

    boolean existsByPoiId(String poiId);
    Optional<Place> findByPoiId(String poiId);
}
