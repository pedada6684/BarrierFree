package com.fullship.hBAF.domain.review.controller.request;

import lombok.Getter;

@Getter
public class ModifyReviewRequest {

    Long reviewId;
    String content;
    Long like;

}
