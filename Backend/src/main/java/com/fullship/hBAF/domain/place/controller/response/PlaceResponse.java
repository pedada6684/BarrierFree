package com.fullship.hBAF.domain.place.controller.response;

import com.fullship.hBAF.domain.place.entity.Place;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder 
public class PlaceResponse {
    private Long placeId;
    private String placeName;
    private String address;
    private String lat;
    private String lon;
    private String poi;
    private String category;
    private String barrierFree;
    // image 추가

    public static PlaceResponse from(Place place) {
        return PlaceResponse.builder()
                .placeId(place.getId())
                .placeName(place.getPlaceName())
                .address(place.getAddress())
                .lat(place.getLatitude())
                .lon(place.getLongitude())
                .poi(place.getPoiId())
                .category(place.getCategory())
                .barrierFree(place.getBarrierFree())
                .build();
    }
}