package com.fullship.hBAF.global.api.service.command;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class searchBFCommand {
    String lat;
    String lng;
    String keyword;
    public searchKakaoPlaceCommand convertToSearchCommand(){
        return searchKakaoPlaceCommand.builder()
                .lat(lat)
                .lng(lng)
                .keyword(keyword)
                .build();
    }
}
