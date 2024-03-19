package com.fullship.hBAF.domain.review.service.command.request;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetReviewRequestCommand {

    Long reviewId;

}
