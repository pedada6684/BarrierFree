package com.fullship.hBAF.domain.member.service.command;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateMemberCommand {
    String email;
    String password;
    String nickname;
    String username;
}
