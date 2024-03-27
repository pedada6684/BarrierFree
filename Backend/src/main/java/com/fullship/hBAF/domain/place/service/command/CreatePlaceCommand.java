package com.fullship.hBAF.domain.place.service.command;

import com.fullship.hBAF.global.api.response.KakaoPlace;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreatePlaceCommand {
    private String placeName;
    private String address;
    private String latitude;
    private String longitude;
    private String poiId;
    private String category;
    private String barrierFree;
    private String wtcltId;
    private Boolean type;

    public static CreatePlaceCommand fromKakaoPlace(KakaoPlace kakaoPlace){
        CreatePlaceCommand createCommand = CreatePlaceCommand.builder()
                .placeName(kakaoPlace.getName())
                .address(kakaoPlace.getFullAddressRoad())
                .latitude(kakaoPlace.getFrontLat())
                .longitude(kakaoPlace.getFrontLon())
                .poiId(kakaoPlace.getId())
                .type(true)
                .build();
        return createCommand;
    }
}