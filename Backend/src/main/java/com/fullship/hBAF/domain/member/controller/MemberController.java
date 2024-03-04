package com.fullship.hBAF.domain.member.controller;

import com.fullship.hBAF.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Tag(name = "Member 컨트롤러", description = "사용자 API 입니다.")
public class MemberController {

  private final MemberService memberService;
}
