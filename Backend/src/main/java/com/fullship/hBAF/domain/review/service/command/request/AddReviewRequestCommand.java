package com.fullship.hBAF.domain.review.service.command.request;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
public class AddReviewRequestCommand {

    Long memberId;
    String content;
    Long feedback;
    String poiId;
    List<MultipartFile> file;

}
