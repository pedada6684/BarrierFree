package com.fullship.hBAF.domain.member.controller;

import com.fullship.hBAF.domain.member.controller.request.NaverLoginRequest;
import com.fullship.hBAF.domain.member.service.MemberService;
import com.fullship.hBAF.global.auth.jwt.AuthToken;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Tag(name = "Member 컨트롤러", description = "사용자 API 입니다.")
public class MemberController {

  private final MemberService memberService;

  @PostMapping("/naverLogin")
  public ResponseEntity<AuthToken> naverLogin(NaverLoginRequest request){
    log.info("request: " + request);
    AuthToken authToken = memberService.login(request.toCommand());
    return new ResponseEntity<>(authToken, HttpStatus.OK);
  }
}
