package com.fullship.hBAF.domain.member.controller;

import com.fullship.hBAF.config.auth.loginRequest.NaverLoginRequest;
import com.fullship.hBAF.config.jwt.AuthToken;
import com.fullship.hBAF.domain.member.service.OAuthLoginService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.queue.PredicatedQueue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
@Slf4j
public class OAuthController {

  private final OAuthLoginService oAuthLoginService;
  @GetMapping("/naver")
  public ResponseEntity<AuthToken> naverLogin(String code, String state){
    log.info("request: " + code);
    NaverLoginRequest request = new NaverLoginRequest(code, state);
    AuthToken authToken = oAuthLoginService.login(request);
    return new ResponseEntity<>(authToken, HttpStatus.OK);
  }
}
