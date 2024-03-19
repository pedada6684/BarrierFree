package com.fullship.hBAF.domain.bookmarkPlace.controller.request;

import lombok.Data;
import lombok.Getter;

@Getter
public class BookmarkPlaceRequest {

    Long memberId;
    String poiId;
    String placeName;
    String address;
    String latitude;
    String longitude;

}
