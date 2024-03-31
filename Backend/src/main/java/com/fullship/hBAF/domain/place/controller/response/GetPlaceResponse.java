package com.fullship.hBAF.domain.place.controller.response;

import com.fullship.hBAF.domain.place.entity.Place;
import com.fullship.hBAF.util.BarrierFreeInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetPlaceResponse {

    Long placeId;
    String placeName;
    String address;
    String lat;
    String lng;
    String poiId;
    String category;
    private List<String> barrierFree;

    public static GetPlaceResponse from(Place place) {
        BarrierFreeInfo barrierFreeInfo = new BarrierFreeInfo();

        return GetPlaceResponse.builder()
                .placeId(place.getId())
                .placeName(place.getPlaceName())
                .address(place.getAddress())
                .lat(place.getLatitude())
                .lng(place.getLongitude())
                .poiId(place.getPoiId())
                .category(place.getCategory())
                .barrierFree(barrierFreeInfo.makeBafArrInfo(place.getBarrierFree()))
                .build();
    }

}
