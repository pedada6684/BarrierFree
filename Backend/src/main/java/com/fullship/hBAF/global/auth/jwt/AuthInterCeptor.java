package com.fullship.hBAF.global.auth.jwt;

import com.fullship.hBAF.domain.member.service.MemberService;
import com.fullship.hBAF.domain.member.service.command.FindMemberByEmailCommand;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthInterCeptor implements HandlerInterceptor {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.debug("Login Interceptor preHandler");
        String authToken = resolveTokenInRequest(request);
        if (authToken != null && jwtTokenProvider.validateToken(authToken)){
            String email = getUserIdFromToken(authToken);
            FindMemberByEmailCommand command = FindMemberByEmailCommand.builder()
                    .email(email)
                    .build();
            Long userId = memberService.findUserByEmail(command);
        }
        return true;
    }

    /**
     * header에서 토큰 추출하는 메서드
     * @param request HttpServletRequest
     * @return token string
     */
    private String resolveTokenInRequest(HttpServletRequest request){
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        return jwtTokenProvider.resolveToken(bearerToken);
    }

    /**
     * token으로 부터 userId를 가져오는 메서드
     * @param accessToken     * @return userEmail
     */
    private String getUserIdFromToken(String accessToken) {
        return jwtTokenProvider.extractSubject(accessToken);
    }
}
