package com.fullship.hBAF.domain.member.service.command;

import com.fullship.hBAF.domain.member.entity.Member;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JoinMemberCommand {
    String nickname;
    String name;
    String email;
    String profileImage;
}
