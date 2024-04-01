package com.fullship.hBAF.domain.member.service.command;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogoutCommand {
    Long memberId;
}
