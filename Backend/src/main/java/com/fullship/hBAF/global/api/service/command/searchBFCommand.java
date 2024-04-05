package com.fullship.hBAF.global.api.service.command;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class searchBFCommand {
    String lat;
    String lng;
    String keyword;
    public SearchKakaoPlaceCommand convertToSearchCommand(){
        return SearchKakaoPlaceCommand.builder()
                .lat(lat)
                .lng(lng)
                .keyword(keyword)
                .build();
    }
}
