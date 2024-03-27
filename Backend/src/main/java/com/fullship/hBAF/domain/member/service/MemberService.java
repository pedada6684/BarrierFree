package com.fullship.hBAF.domain.member.service;

import com.fullship.hBAF.domain.member.entity.Member;
import com.fullship.hBAF.domain.member.repository.MemberRepository;
import com.fullship.hBAF.domain.member.service.command.FindMemberByEmailCommand;
import com.fullship.hBAF.domain.member.service.command.JoinMemberCommand;
import com.fullship.hBAF.domain.member.service.command.NaverLoginCommand;
import com.fullship.hBAF.domain.member.service.command.UpdateProfileImgCommand;
import com.fullship.hBAF.global.auth.jwt.AuthToken;
import com.fullship.hBAF.global.auth.jwt.AuthTokenGenerator;
import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import com.fullship.hBAF.util.ImageUtil;
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
  private final ImageUtil imageUtil;


  public Long findUserByEmail(FindMemberByEmailCommand command){
    Member member = memberRepository.findByEmail(command.getEmail())
            .orElseThrow(() -> new CustomException(ErrorCode.ENTITIY_NOT_FOUND));
    return member.getId();
  }

  @Transactional(readOnly = false)
  public AuthToken login(NaverLoginCommand command) {
    Member member = memberRepository.findByEmail(command.getEmail())
            .orElseGet(// 신규 유저인 경우 회원 가입
                    ()->joinMember(command.convertToJoinMemberCommand())
            );
    return authTokenGenerator.generate(member.getId());
  }

  @Transactional(readOnly = false)
  public Member joinMember(JoinMemberCommand command) {
    Member newMember = Member.createNewMember(
            command.getEmail(),
            null,
            command.getNickname(),
            command.getName()
    );
    newMember = memberRepository.save(newMember);

    //트랜젝션 유의
    URL S3Url = imageUtil.uploadImageToS3(command.getProfileImage(), "profile", newMember.getId().toString());
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
    Member member = memberRepository.findById(command.getMemberId())
            .orElseThrow(() -> new CustomException(ErrorCode.ENTITIY_NOT_FOUND));
    URL S3Url = imageUtil.uploadImageToS3(command.getProfileImg(), "profile", member.getId().toString());
    Objects.requireNonNull(S3Url);
    member.updateProfileUrl(S3Url.toString());
    return S3Url.toString();
  }
}