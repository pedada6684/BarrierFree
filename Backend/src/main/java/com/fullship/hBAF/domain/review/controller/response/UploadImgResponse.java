package com.fullship.hBAF.domain.review.controller.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UploadImgResponse {

    List<String> list;

}
