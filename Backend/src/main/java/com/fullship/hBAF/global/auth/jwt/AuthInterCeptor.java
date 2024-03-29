package com.fullship.hBAF.global.auth.jwt;

import com.fullship.hBAF.domain.member.service.MemberService;
import com.fullship.hBAF.domain.member.service.command.FindMemberByIdCommand;
import com.fullship.hBAF.global.auth.entity.RedisRefreshToken;
import com.fullship.hBAF.global.auth.repository.RefreshTokenRepository;
import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import com.fullship.hBAF.util.CookieProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthInterCeptor implements HandlerInterceptor {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String REFRESH_TOKEN_NAME = "refreshToken";
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthTokenGenerator authTokenGenerator;
    private final CookieProvider cookieProvider;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.debug("Login Interceptor preHandler");
        if (HttpMethod.OPTIONS.matches(request.getMethod())){
            return true;
        }
        //JWT 추출
        String accessToken = resolveTokenInRequest(request);
        String refreshToken = getRefreshToken(request);
        if (refreshToken == null){
            throw new CustomException(ErrorCode.TOKEN_NOT_FOUND);
        }
        //JWT 유효성 검사
        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)){ // AT유효
            return allowAccess(response, accessToken);
        }else{ //AT 만료
            if (jwtTokenProvider.validateToken(refreshToken)){ //RT 유효
                String strMemberId = getMemberIdFromToken(refreshToken);
                RedisRefreshToken redisRefreshToken = refreshTokenRepository.findById(strMemberId).orElseThrow(
                        () -> new CustomException(ErrorCode.TOKEN_NOT_FOUND)
                );
                if (jwtTokenProvider.validateToken(redisRefreshToken.getRefreshToken())){
                    return allowAccess(response, accessToken);
                }
            }
        }
        throw new CustomException(ErrorCode.INVALID_TOKEN);
    }

    /**
     * 검증된 접근일 시 토큰을 재발급하고 접근을 허가하는 메서드
     * @param response
     * @param accessToken
     * @return
     */
    private boolean allowAccess(HttpServletResponse response, String accessToken) {
        //유저 존재여부 확인
        String strMemberId = getMemberIdFromToken(accessToken);
        long memberId = Long.parseLong(strMemberId);
        FindMemberByIdCommand command = FindMemberByIdCommand.builder()
                .id(Long.parseLong(strMemberId))
                .build();
        memberService.findMemberById(command);

        //AT RT 재발급
        AccessToken newAccessToken = authTokenGenerator.generateAT(memberId);
        RefreshToken newRefreshToken = authTokenGenerator.generateRT(memberId);
        Cookie cookie = cookieProvider.createCookie(
                "refreshToken",
                newRefreshToken.getGrantType() +":"+ newRefreshToken.getRefreshToken(),
                Long.valueOf(newRefreshToken.getExpiresIn()/1000L).intValue()
        );
        response.setHeader("Authorization", "Bearer " + newAccessToken.getAccessToken());
        response.addCookie(cookie);
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
     * refreshToken 추출 메서드
     * @param request
     * @return refreshToken
     */
    private String getRefreshToken(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(REFRESH_TOKEN_NAME)){
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * token으로 부터 userId를 가져오는 메서드
     * @param accessToken     * @return userEmail
     */
    private String getMemberIdFromToken(String accessToken) {
        return jwtTokenProvider.extractSubject(accessToken);
    }
}
