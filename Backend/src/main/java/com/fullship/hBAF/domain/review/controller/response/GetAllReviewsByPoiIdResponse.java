package com.fullship.hBAF.domain.review.controller.response;

import com.fullship.hBAF.domain.review.service.command.response.GetAllReviewsByPoiIdResponseCommand;
import com.fullship.hBAF.domain.review.service.command.response.GetReviewResponseCommand;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetAllReviewsByPoiIdResponse {

    List<GetAllReviewsByPoiIdResponseCommand> list;

}
