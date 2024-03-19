package com.fullship.hBAF.domain.review.controller.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GetReviewResponse {

    Long memberId;
    String nickname;
    String content;
    Long like;
    LocalDateTime regDate;
    LocalDateTime modifyDate;
    Long status;
    String poiId;

}
