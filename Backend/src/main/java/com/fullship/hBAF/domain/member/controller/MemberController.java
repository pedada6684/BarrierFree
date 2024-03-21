package com.fullship.hBAF.domain.member.controller;

import com.fullship.hBAF.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
@Tag(name = "Member 컨트롤러", description = "사용자 API 입니다.")
public class MemberController {

  private final MemberService memberService;

  @Value("${oauth.naver.url.base}")
  String baseUrl;
  @Value("${oauth.naver.url.callback}")
  String redirectUrl;
  @Value("${oauth.naver.client-id}")
  String clientId;


  @GetMapping("/test")
  public String test(Model model){
    return "test1";
  }

  @GetMapping("/login")
  public void test(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String url = getNaverAuthorizeUrl("authorize");
    response.sendRedirect(url);
  }
  public String getNaverAuthorizeUrl(String type) throws UnsupportedEncodingException {
    UriComponents uriComponents = UriComponentsBuilder
            .fromUriString(baseUrl + "/" + type)
            .queryParam("response_type", "code")
            .queryParam("client_id", clientId)
            .queryParam("redirect_uri", URLEncoder.encode(redirectUrl, "UTF-8"))
            .queryParam("state", URLEncoder.encode("1234", "UTF-8"))
            .build();
    return uriComponents.toString();
  }
}
