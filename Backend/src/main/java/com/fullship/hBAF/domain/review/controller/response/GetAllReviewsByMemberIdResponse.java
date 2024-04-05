package com.fullship.hBAF.domain.review.controller.response;

import com.fullship.hBAF.domain.review.service.command.response.GetAllReviewsByMemberIdResponseCommand;
import com.fullship.hBAF.domain.review.service.command.response.GetAllReviewsByPoiIdResponseCommand;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetAllReviewsByMemberIdResponse {

    List<GetAllReviewsByMemberIdResponseCommand> list;

}
