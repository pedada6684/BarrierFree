package com.fullship.hBAF.domain.review.service.command.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddReviewRequestCommand {

    Long memberId;
    String content;
    Long feedback;
    String poiId;

}
