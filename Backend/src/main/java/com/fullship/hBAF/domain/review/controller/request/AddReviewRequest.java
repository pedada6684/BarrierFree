package com.fullship.hBAF.domain.review.controller.request;

import lombok.Data;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class AddReviewRequest {

    Long memberId;
    String content;
    Long feedback;
    String poiId;
    List<MultipartFile> img;

}
