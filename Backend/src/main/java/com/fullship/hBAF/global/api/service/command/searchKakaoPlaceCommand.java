package com.fullship.hBAF.global.api.service.command;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class searchKakaoPlaceCommand {
    String lat;
    String lng;
    String keyword;
}
