package com.fullship.hBAF.domain.review.service.command.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GetReviewResponseCommand {

    Long memberId;
    String nickname;
    String content;
    Long feedback;
    LocalDateTime regDate;
    LocalDateTime modifyDate;
    Long status;
    String poiId;

}
