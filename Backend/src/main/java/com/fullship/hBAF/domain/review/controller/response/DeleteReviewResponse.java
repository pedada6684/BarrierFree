package com.fullship.hBAF.domain.review.controller.response;

import com.fullship.hBAF.domain.review.service.command.response.DeleteReviewResponseCommand;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteReviewResponse {

    DeleteReviewResponseCommand response;

}
