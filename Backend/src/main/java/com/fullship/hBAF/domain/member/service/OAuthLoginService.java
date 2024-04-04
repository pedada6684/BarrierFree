package com.fullship.hBAF.domain.member.service;

import com.fullship.hBAF.global.auth.controller.response.LoginResult;
import com.fullship.hBAF.global.auth.controller.request.OAuthLoginRequest;
import com.fullship.hBAF.global.auth.dto.memberInfo.OAuthMemberInfo;
import com.fullship.hBAF.global.auth.jwt.AuthTokenGenerator;
import com.fullship.hBAF.global.auth.oauth.RequestOAuthInfoService;
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

  /**
   * oauth login or join 메서드
   * @param request
   * @return
   */
  public LoginResult login(OAuthLoginRequest request){
    OAuthMemberInfo oAuthMemberInfo = requestOAuthInfoService.request(request);
    Member member = memberRepository.findByEmail(oAuthMemberInfo.getEmail())
            .orElseGet(()->createMember(oAuthMemberInfo)); // 신규 유저인 경우 회원 가입
    LoginResult result = LoginResult.builder()
            .accessToken(authTokenGenerator.generateAT(member.getId()))
            .refreshToken(authTokenGenerator.generateRT(member.getId()))
            .build();
    return result;
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
