package com.fullship.hBAF.config.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class AuthTokenGenerator {
    private static final String BEARER_TYPE= "Bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME= 1000 * 60 * 30;            // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME= 1000 * 60 * 60 * 24 * 7;  // 7일

    private final JwtTokenProvider jwtTokenProvider;

    public AuthToken generate(Long userId){
        long now = new Date().getTime();
        Date accessTokenExpiredDate  = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        Date refreshTokenExpiredDate  = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);

        String subject = userId.toString();
        String accessToken = jwtTokenProvider.generate(subject, accessTokenExpiredDate);
        String refreshToken = jwtTokenProvider.generate(subject, refreshTokenExpiredDate);

        return AuthToken.of(accessToken, refreshToken, BEARER_TYPE, 1000L);
    }
}
