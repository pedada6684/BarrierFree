package com.fullship.hBAF.domain.place.controller.response;

import com.fullship.hBAF.domain.place.entity.Image;
import com.fullship.hBAF.domain.place.entity.Place;
import com.fullship.hBAF.util.BarrierFreeInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
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
    String phone;
    String placeUrl;
    List<String> barrierFree;
    List<String> img;

    public static GetPlaceResponse from(Place place) {
        return GetPlaceResponse.builder()
                .placeId(place.getId())
                .placeName(place.getPlaceName())
                .address(place.getAddress())
                .lat(place.getLatitude())
                .lng(place.getLongitude())
                .poiId(place.getPoiId())
                .category(place.getCategory())
                .phone(place.getPhone())
                .placeUrl(place.getPlaceUrl())
                .barrierFree(BarrierFreeInfo.makeBafArrInfo(place.getBarrierFree()))
                .img(createToCommand(place.getImages()))
                .build();
    }
    public static List<String> createToCommand(List<Image> list){
        List<String> result = new ArrayList<>();
        for(Image img : list){
            result.add(img.getImageUrl());
        }
        return result;
    }
}
