package com.fullship.hBAF.domain.member.controller;

import com.fullship.hBAF.domain.member.controller.request.UpdateProfileImgRequest;
import com.fullship.hBAF.domain.member.controller.response.GetMemberInfoResponse;
import com.fullship.hBAF.domain.member.controller.response.UpdateProfileResponse;
import com.fullship.hBAF.domain.member.entity.Member;
import com.fullship.hBAF.domain.member.service.MemberService;
import com.fullship.hBAF.domain.member.service.command.FindMemberByIdCommand;
import com.fullship.hBAF.domain.member.service.command.WithdrawMemberCommand;
import com.fullship.hBAF.global.auth.jwt.JwtTokenProvider;
import com.fullship.hBAF.util.Auth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Tag(name = "Member 컨트롤러", description = "사용자 API 입니다.")
public class MemberController {

  private final MemberService memberService;
  private final JwtTokenProvider jwtTokenProvider;

  @PostMapping("/profile")
  @Operation(summary = "프로필 이미지 변경", description = "프로필 이미지 변경")
  @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = UpdateProfileResponse.class)))
  public ResponseEntity<UpdateProfileResponse> updateProfileImg(@ModelAttribute UpdateProfileImgRequest request) {
    log.info("UpdateProfileRequest: " + request);
    //유저 아이디 검증 메서드 하나 추가해야함 with jwt
    String profileImgUrl = memberService.updateProfileImg(request.toCommand());

    UpdateProfileResponse response = UpdateProfileResponse.builder()
        .profileImgUrl(profileImgUrl)
        .build();
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/withdraw")
  @Operation(summary = "회원 탈퇴", description = "회원 탈퇴")
  @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = UpdateProfileResponse.class)))
  public ResponseEntity<?> withdrawMember(@RequestParam("memberId") Long memberId) {
    WithdrawMemberCommand command = WithdrawMemberCommand.builder()
            .memberId(memberId)
            .build();
    memberService.withdrawMember(command);
    return ResponseEntity.ok().build();
  }

  @Auth
  @GetMapping
  @Operation(summary = "유저 정보 요청", description = "유저 정보 요청")
  @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = GetMemberInfoResponse.class)))
  public ResponseEntity<GetMemberInfoResponse> getMemberInfo(@RequestParam("memberId") Long memberId) {
//    HttpServletResponse response;
//    String accessToken = jwtTokenProvider.resolveToken(response);
//    String memberId = jwtTokenProvider.extractSubject(accessToken);
    FindMemberByIdCommand command = FindMemberByIdCommand.builder()
            .id(memberId)
            .build();
    Member member = memberService.findMemberById(command);
    GetMemberInfoResponse response = GetMemberInfoResponse.from(member);
    return new ResponseEntity<>(response, HttpStatus.OK);

  }
}
