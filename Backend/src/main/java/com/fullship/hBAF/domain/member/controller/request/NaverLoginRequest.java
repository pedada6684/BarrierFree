package com.fullship.hBAF.domain.member.controller.request;

import com.fullship.hBAF.domain.member.service.command.NaverLoginCommand;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NaverLoginRequest {
    String nickname;
    String name;
    String email;
    String profileImage;

    public NaverLoginCommand toCommand(){
        return NaverLoginCommand.builder()
                .nickname(nickname)
                .name(name)
                .email(email)
                .profileImage(profileImage)
                .build();
    }
}
