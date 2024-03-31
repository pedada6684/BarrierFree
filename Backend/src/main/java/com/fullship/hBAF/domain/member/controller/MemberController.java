package com.fullship.hBAF.domain.member.controller;

import com.fullship.hBAF.domain.member.controller.request.UpdateProfileImgRequest;
import com.fullship.hBAF.domain.member.controller.response.UpdateProfileResponse;
import com.fullship.hBAF.domain.member.service.MemberService;
import com.fullship.hBAF.util.CookieProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
  @Operation(summary = "프로필 이미지 변경", description = "프로필 이미지 변경")
  @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = UpdateProfileResponse.class)))
  public ResponseEntity<UpdateProfileResponse> updateProfileImg2() {
    //유저 아이디 검증 메서드 하나 추가해야함 with jwt

    UpdateProfileResponse response = UpdateProfileResponse.builder()
            .build();
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
