package com.fullship.hBAF.domain.member.service.command;

import com.fullship.hBAF.domain.member.entity.Member;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NaverLoginCommand {
    String nickname;
    String name;
    String email;
    String profileImage;

    public JoinMemberCommand convertToJoinMemberCommand(){
        return JoinMemberCommand.builder()
                .email(email)
                .name(name)
                .profileImage(profileImage)
                .nickname(nickname)
                .build();
    }
}
