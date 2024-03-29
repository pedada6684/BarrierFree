package com.fullship.hBAF.domain.place.controller.response;

import lombok.Builder;
import lombok.Getter;

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
    String barrierFree;

}
