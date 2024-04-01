package com.fullship.hBAF.global.auth.controller;

import com.fullship.hBAF.domain.member.controller.response.UpdateProfileResponse;
import com.fullship.hBAF.domain.member.service.command.LogoutCommand;
import com.fullship.hBAF.global.auth.controller.request.AppLoginRequest;
import com.fullship.hBAF.global.auth.controller.response.LoginResult;
import com.fullship.hBAF.domain.member.service.MemberService;
import com.fullship.hBAF.global.auth.controller.request.NaverLoginRequest;
import com.fullship.hBAF.global.auth.jwt.*;
import com.fullship.hBAF.domain.member.service.OAuthLoginService;
import com.fullship.hBAF.global.response.CommonResponseEntity;
import com.fullship.hBAF.global.response.SuccessCode;
import com.fullship.hBAF.util.Auth;
import com.fullship.hBAF.util.CookieProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.fullship.hBAF.global.response.CommonResponseEntity.getResponseEntity;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
@Slf4j
public class AuthController {

  private final OAuthLoginService oAuthLoginService;
  private final CookieProvider cookieProvider;
  private final MemberService memberService;

  @GetMapping("/OauthNaver")
  public ResponseEntity<AccessToken> webLogin(String code, String state, HttpServletResponse response){
    log.info("request: " + code);
    NaverLoginRequest request = new NaverLoginRequest(code, state);
    LoginResult result = oAuthLoginService.login(request);
    AccessToken accessToken = result.getAccessToken();
    RefreshToken refreshToken = result.getRefreshToken();
    Cookie cookie = cookieProvider.createCookie(
            "refreshToken",
            refreshToken.getRefreshToken(),
            Long.valueOf(refreshToken.getExpiresIn()/1000L).intValue()
    );
    //헤더에 refreshToken 삽입
    response.addCookie(cookie);
    return new ResponseEntity<>(accessToken, HttpStatus.OK);
  }

  /**
   * 플러터 로그인
   * @param request
   *     String nickname;
   *     String name;
   *     String email;
   *     String profileImage;
   * @return body AT, header RT
   */
  @PostMapping("/appLogin")
  @Operation(summary = "토큰 가져오기", description = "로그인 후 토큰 생성")
  @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = AuthToken.class)))
  public ResponseEntity<AccessToken> webLogin(AppLoginRequest request, HttpServletResponse response) {
    log.info("AppLoginRequest: " + request);
    LoginResult result = memberService.login(request.toCommand());

    //토큰 get & cookie 생성
    AccessToken accessToken = result.getAccessToken();
    RefreshToken refreshToken = result.getRefreshToken();
    Cookie cookie = cookieProvider.createCookie(
            "refreshToken",
            refreshToken.getRefreshToken(),
            Long.valueOf(refreshToken.getExpiresIn()/1000L).intValue()
    );
    //헤더에 refreshToken 삽입
    response.addCookie(cookie);
    return new ResponseEntity<>(accessToken, HttpStatus.OK);
  }

  @GetMapping("/logout")
  @Operation(summary = "로그아웃", description = "로그아웃")
  @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = UpdateProfileResponse.class)))
  public ResponseEntity<?> logout(@RequestParam("memberId") Long memberId) {
    LogoutCommand command = LogoutCommand.builder()
            .memberId(memberId)
            .build();
    memberService.logout(command);
    return ResponseEntity.ok().build();
  }

  @Auth
  @GetMapping("/test")
  @Operation(summary = "로그인 여부 확인 테스트", description = "로그인 여부 확인 테스트")
  public ResponseEntity<CommonResponseEntity> test() {
    System.out.println("통신 테스트");
    return getResponseEntity(SuccessCode.OK, "테스트입니다.");
  }
}
