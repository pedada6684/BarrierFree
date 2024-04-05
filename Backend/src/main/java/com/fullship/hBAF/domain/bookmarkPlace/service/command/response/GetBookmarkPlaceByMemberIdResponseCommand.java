package com.fullship.hBAF.domain.bookmarkPlace.service.command.response;

import com.fullship.hBAF.domain.place.entity.Image;
import com.fullship.hBAF.domain.place.entity.Place;
import com.fullship.hBAF.util.BarrierFreeInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class GetBookmarkPlaceByMemberIdResponseCommand {

    private Long placeId;
    private String placeName;
    private String address;
    private String lat;
    private String lng;
    private String poi;
    private String category;
    private String phone;
    private String placeUrl;
    private List<String> barrierFree;
    private List<String> img;

    public static GetBookmarkPlaceByMemberIdResponseCommand from(Place place) {

        return GetBookmarkPlaceByMemberIdResponseCommand.builder()
                .placeId(place.getId())
                .placeName(place.getPlaceName())
                .address(place.getAddress())
                .lat(place.getLatitude())
                .lng(place.getLongitude())
                .poi(place.getPoiId())
                .category(place.getCategory())
                .phone(place.getPhone())
                .placeUrl(place.getPlaceUrl())
                .barrierFree(BarrierFreeInfo.makeBafArrInfo(place.getBarrierFree()))
                .img(createToCommand(place.getImages()))
                .build();
    }

    public static List<String> createToCommand(List<Image> list){
        List<String> result = new ArrayList<>();
        for(Image img : list)
            result.add(img.getImageUrl());

        return result;
    }

}
