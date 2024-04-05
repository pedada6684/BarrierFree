package com.fullship.hBAF.global.api.service.command;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchKakaoPlaceCommand {
    String lat;
    String lng;
    String keyword;
    String category;
}
