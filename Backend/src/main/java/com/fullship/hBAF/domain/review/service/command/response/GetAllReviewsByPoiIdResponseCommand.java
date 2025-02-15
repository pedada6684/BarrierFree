package com.fullship.hBAF.domain.review.service.command.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class GetAllReviewsByPoiIdResponseCommand {

    Long memberId;
    String nickname;
    String content;
    List<String> lik;
    List<String> unlik;
    LocalDateTime regDate;
    LocalDateTime modifyDate;
    Long status;
    String poiId;
    List<String> img;

}
