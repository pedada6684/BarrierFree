package com.fullship.hBAF.domain.member.controller.request;

import com.fullship.hBAF.domain.member.service.command.NaverLoginCommand;
import com.fullship.hBAF.domain.member.service.command.UpdateProfileImgCommand;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
public class UpdateProfileImgRequest {
    Long memberId;
    MultipartFile profileImg;
    public UpdateProfileImgCommand toCommand(){
        return UpdateProfileImgCommand.builder()
                .memberId(memberId)
                .profileImg(profileImg)
                .build();
    }
}
