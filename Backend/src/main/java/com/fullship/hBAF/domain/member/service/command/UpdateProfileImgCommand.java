package com.fullship.hBAF.domain.member.service.command;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class UpdateProfileImgCommand {
    Long memberId;
    MultipartFile profileImg;
}
