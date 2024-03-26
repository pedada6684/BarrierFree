package com.fullship.hBAF.util;

import com.fullship.hBAF.domain.member.service.MemberService;
import com.fullship.hBAF.domain.member.service.command.JoinMemberCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DummyGenerator {

    private final MemberService memberService;

    public void createDummyMembers(){
        JoinMemberCommand member1 = JoinMemberCommand.builder()
                .email("ssafy1@ssafy.com")
                .name("ssafy1")
                .nickname("ssafy1")
                .profileImage("https://cdn.artsnculture.com/news/photo/202102/2538_6820_2057.png")
                .build();
        JoinMemberCommand member2 = JoinMemberCommand.builder()
                .email("ssafy2@ssafy.com")
                .name("ssafy2")
                .nickname("ssafy2")
                .profileImage("https://cdn.artsnculture.com/news/photo/202102/2538_6820_2057.png")
                .build();
        JoinMemberCommand member3 = JoinMemberCommand.builder()
                .email("ssafy3@ssafy.com")
                .name("ssafy3")
                .nickname("ssafy3")
                .profileImage("https://cdn.artsnculture.com/news/photo/202102/2538_6820_2057.png")
                .build();
        memberService.joinMember(member1);
        memberService.joinMember(member2);
        memberService.joinMember(member3);
    }


}
