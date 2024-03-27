package com.fullship.hBAF.domain.place.service.command.Request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetPlaceListRequestComment {

    String lat;
    String lng;

}
