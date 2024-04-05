package com.fullship.hBAF.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {
    String jwt = "JWT";
    SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
    Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
        .name(jwt)
        .type(SecurityScheme.Type.HTTP)
        .scheme("bearer")
        .bearerFormat("JWT")
    );

    List<Server> servers = new ArrayList<>();
    Server sslServer = new Server();
    Server server = new Server();
    server.setUrl("http://localhost:8080/api");
    sslServer.setUrl("https://hbaf.site/api");
    servers.add(server);
    servers.add(sslServer);

    return new OpenAPI()
        .components(new Components())
        .info(apiInfo())
        .servers(servers)
        .addSecurityItem(securityRequirement)
        .components(components);
  }

  private Info apiInfo() {
    return new Info()
        .title("베프") // API의 제목
        .description("팀 만선의 API 문서입니다") // API에 대한 설명
        .version("1.0.0"); // API의 버전
  }
}