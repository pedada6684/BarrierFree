package com.fullship.hBAF.domain.member.service;

import com.fullship.hBAF.domain.member.entity.Member;
import com.fullship.hBAF.domain.member.repository.MemberRepository;
import com.fullship.hBAF.domain.member.service.command.FindMemberByEmailCommand;
import com.fullship.hBAF.domain.member.service.command.JoinMemberCommand;
import com.fullship.hBAF.domain.member.service.command.NaverLoginCommand;
import com.fullship.hBAF.global.auth.jwt.AuthToken;
import com.fullship.hBAF.global.auth.jwt.AuthTokenGenerator;
import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import com.fullship.hBAF.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.Objects;

@Service
@RequiredArgsConstructor
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
            .orElse(// 신규 유저인 경우 회원 가입
                    joinMember(command.convertToJoinMemberCommand())
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

    //트랜젝션 유의
    URL S3Url = imageUtil.uploadImageToS3(command.getProfileImage(), "profile", command.getEmail());
    Objects.requireNonNull(S3Url);
    newMember.updateProfileUrl(S3Url.toString());
    return memberRepository.save(newMember);
  }
}