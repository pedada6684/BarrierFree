package com.fullship.hBAF.domain.review.service.command.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ModifyReviewRequestCommand {

    Long reviewId;
    String content;
    Long like;

}
