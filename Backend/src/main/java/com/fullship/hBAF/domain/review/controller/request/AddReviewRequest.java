package com.fullship.hBAF.domain.review.controller.request;

import lombok.Getter;

@Getter
public class AddReviewRequest {

    Long memberId;
    String content;
    Long feedback;
    String poiId;

}
