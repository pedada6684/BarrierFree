package com.fullship.hBAF.domain.member.service;

import com.fullship.hBAF.config.auth.loginRequest.OAuthLoginRequest;
import com.fullship.hBAF.config.auth.memberInfo.OAuthMemberInfo;
import com.fullship.hBAF.config.jwt.AuthToken;
import com.fullship.hBAF.config.jwt.AuthTokenGenerator;
import com.fullship.hBAF.config.oauth.RequestOAuthInfoService;
import com.fullship.hBAF.domain.member.entity.Member;
import com.fullship.hBAF.domain.member.repository.MemberRepository;
import com.fullship.hBAF.domain.member.service.command.CreateMemberCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {
  private final MemberRepository memberRepository;
  private final AuthTokenGenerator authTokenGenerator;
  private final RequestOAuthInfoService requestOAuthInfoService;
//  private final PasswordEncorder 
//TODO: passwordEncoder 사용해볼것

  public AuthToken login(OAuthLoginRequest request){
    OAuthMemberInfo oAuthMemberInfo = requestOAuthInfoService.request(request);
    Member member = memberRepository.findByEmail(oAuthMemberInfo.getEmail())
            .orElse(createMember(oAuthMemberInfo)); // 신규 유저인 경우 회원 가입
    return authTokenGenerator.generate(member.getId());
  }
  public Member createMember(CreateMemberCommand command){
    Member newMember = Member.createNewMember(
            command.getEmail(),
            command.getPassword(),
            command.getNickname(),
            command.getUsername()
    );
    return memberRepository.save(newMember);
  }
  public Member createMember(OAuthMemberInfo oAuthMemberInfo){
    Member newMember = Member.createNewMember(
            oAuthMemberInfo.getEmail(),
            null,
            oAuthMemberInfo.getNickname(),
            oAuthMemberInfo.getName()
    );
    return memberRepository.save(newMember);
  }
}
