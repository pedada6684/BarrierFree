package com.fullship.hBAF.domain.member.service;

import com.fullship.hBAF.domain.member.service.command.*;
import com.fullship.hBAF.global.auth.controller.response.LoginResult;
import com.fullship.hBAF.domain.member.entity.Member;
import com.fullship.hBAF.domain.member.repository.MemberRepository;
import com.fullship.hBAF.global.auth.jwt.AuthTokenGenerator;
import com.fullship.hBAF.global.auth.service.RefreshTokenService;
import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import com.fullship.hBAF.util.S3Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberService {

  private final MemberRepository memberRepository;
  private final AuthTokenGenerator authTokenGenerator;
  private final S3Util s3Util;
  private final RefreshTokenService refreshTokenService;


  public Member findMemberById(FindMemberByIdCommand command){
    Member member = memberRepository.findById(command.getId())
            .orElseThrow(() -> new CustomException(ErrorCode.ENTITIY_NOT_FOUND));
    return member;
  }

  @Transactional(readOnly = false)
  public LoginResult login(NaverLoginCommand command) {
    log.info("NaverLoginCommand: "+command);
    Member member = memberRepository.findByEmail(command.getEmail())
            .orElseGet(// 신규 유저인 경우 회원 가입
                    ()->joinMember(command.convertToJoinMemberCommand())
            );

    LoginResult result = LoginResult.builder()
            .accessToken(authTokenGenerator.generateAT(member.getId()))
            .refreshToken(authTokenGenerator.generateRT(member.getId()))
            .build();
    return result;
  }

  @Transactional(readOnly = false)
  public Member joinMember(JoinMemberCommand command) {
    log.info("JoinMemberCommand: "+command);
    Member newMember = Member.createNewMember(
            command.getEmail(),
            null,
            command.getNickname(),
            command.getName()
    );
    newMember = memberRepository.save(newMember);

    //트랜젝션 유의
    URL S3Url = s3Util.uploadImageToS3(command.getProfileImage(), "profile", newMember.getId().toString());
    Objects.requireNonNull(S3Url);
    newMember.updateProfileUrl(S3Url.toString());
    return newMember;
  }

  /**
   * 프로필 사진 변경 메서드
   * @param command userId, File
   * @return S3 url
   */
  @Transactional(readOnly = false)
  public String updateProfileImg(UpdateProfileImgCommand command) {
    log.info("UpdateProfileImgCommand: "+command);
    Member member = memberRepository.findById(command.getMemberId())
            .orElseThrow(() -> new CustomException(ErrorCode.ENTITIY_NOT_FOUND));
    URL S3Url = s3Util.uploadImageToS3(command.getProfileImg(), "profile", member.getId().toString());
    Objects.requireNonNull(S3Url);
    member.updateProfileUrl(S3Url.toString());
    return S3Url.toString();
  }

  public void withdrawMember(WithdrawMemberCommand command) {
    log.info("WithdrawMemberCommand: "+command);
    Member member = memberRepository.findById(command.getMemberId())
            .orElseThrow(() -> new CustomException(ErrorCode.ENTITIY_NOT_FOUND));
    member.withdraw();
    return;
  }

  public void logout(LogoutCommand command) {
    log.info("LogoutCommand: "+command);
    refreshTokenService.removeRefreshToken(command.getMemberId());
    return;
  }
}