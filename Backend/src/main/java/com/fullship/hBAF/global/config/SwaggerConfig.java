package com.fullship.hBAF.global.config;

import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
  private Info apiInfo() {
    return new Info()
        .title("베프") // API의 제목
        .description("팀 만선의 API 문서입니다") // API에 대한 설명
        .version("1.0.0"); // API의 버전
  }
}