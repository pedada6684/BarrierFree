package com.fullship.hBAF.domain.bookmarkPlace.service.command.request;

import com.fullship.hBAF.domain.member.entity.Member;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookmarkPlaceRequestCommand {

    Long memberId;
    String poiId;
    String placeName;
    String address;
    String latitude;
    String longitude;
    Long type;

}
