package com.fullship.hBAF.domain.place.repository;

import com.fullship.hBAF.domain.place.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    Place findPlaceByPoiId(String PoiId);
    @Query("""
    select p from Place p
    left join fetch p.images
    where p.poiId = :poiId
""")
    Optional<Place> findPlaceByPoiIdWithImage(@Param("poiId") String poiId);

    boolean existsByPoiId(String poiId);
    Optional<Place> findByPoiId(String poiId);

    List<Place> findByType(boolean type);
    @Query("""
    select p from Place p
    left join fetch p.images
    where p.type = true
    """)
    List<Place> findByTypeWithImage(boolean type);

    @Query("select wtcltId from Place where type = true and barrierFree is null")
    List<String> findWtcltIdByType();
    List<Place> findPlacesByTypeTrueAndPlaceNameContaining(String text);
    Optional<Place> findByWtcltId(String wcltId);
}