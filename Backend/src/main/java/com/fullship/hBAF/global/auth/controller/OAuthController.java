package com.fullship.hBAF.global.auth.controller;

import com.fullship.hBAF.global.auth.dto.loginRequest.NaverLoginRequest;
import com.fullship.hBAF.global.auth.jwt.AuthToken;
import com.fullship.hBAF.domain.member.service.OAuthLoginService;
import com.fullship.hBAF.global.auth.jwt.AuthTokenGenerator;
import com.fullship.hBAF.global.auth.jwt.JwtTokenProvider;
import com.fullship.hBAF.global.auth.entity.RedisRefreshToken;
import com.fullship.hBAF.global.auth.repository.RefreshTokenRepository;
import com.fullship.hBAF.global.auth.service.RefreshTokenService;
import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
@Slf4j
public class OAuthController {

  private final OAuthLoginService oAuthLoginService;
  private final JwtTokenProvider jwtTokenProvider;
  private final AuthTokenGenerator authTokenGenerator;
  private final RefreshTokenService refreshTokenService;

  @GetMapping("/naver")
  public ResponseEntity<AuthToken> naverLogin(String code, String state){
    log.info("request: " + code);
    NaverLoginRequest request = new NaverLoginRequest(code, state);
    AuthToken authToken = oAuthLoginService.login(request);
    return new ResponseEntity<>(authToken, HttpStatus.OK);
  }

  /**
   * 쿠키에 담아온 refreshToken을 검증하는 메서드
   * @param cookie refreshToken은 accessToken과 같은 방식으로 Bearer: {token} 형식
   * @return
   */

  @GetMapping("/refreshToken")
  public ResponseEntity<AuthToken> authRefreshToken(@CookieValue(value = "refreshToken") Cookie cookie){
    //user refreshToken 검증
    String bearerToken = cookie.getValue();
    String refreshToken = jwtTokenProvider.resolveToken(bearerToken);
    jwtTokenProvider.validateToken(refreshToken);

    //redis와 검증
    Long memberId = Long.parseLong(jwtTokenProvider.extractSubject(refreshToken));
    String redisRefreshToken = refreshTokenService.findTokenByMemberId(memberId);
    jwtTokenProvider.validateToken(redisRefreshToken);

    //새로운 토큰 생성
    AuthToken authToken = authTokenGenerator.generate(memberId);
    return new ResponseEntity<>(authToken, HttpStatus.OK);
  }
}
