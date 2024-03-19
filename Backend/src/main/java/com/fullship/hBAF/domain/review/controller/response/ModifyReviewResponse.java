package com.fullship.hBAF.domain.review.controller.response;

import com.fullship.hBAF.domain.review.service.command.response.ModifyReviewResponseCommand;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ModifyReviewResponse {

    ModifyReviewResponseCommand response;

}
