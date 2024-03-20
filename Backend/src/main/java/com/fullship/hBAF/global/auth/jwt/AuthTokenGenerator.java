package com.fullship.hBAF.global.auth.jwt;

import com.fullship.hBAF.global.auth.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class AuthTokenGenerator {
    private static final String BEARER_TYPE= "Bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME= 1000 * 60 * 10;            // 10분
    private static final long REFRESH_TOKEN_EXPIRE_TIME= 1000 * 60 * 60 * 24 * 90;  // 90일

    private final JwtTokenProvider jwtTokenProvider;

    private final RefreshTokenService refreshTokenService;

    public AuthToken generate(Long userId){
        long now = new Date().getTime();
        Date accessTokenExpiredDate  = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        Date refreshTokenExpiredDate  = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);

        String subject = userId.toString();
        String accessToken = jwtTokenProvider.generate(subject, accessTokenExpiredDate);
        String refreshToken = jwtTokenProvider.generate(subject, refreshTokenExpiredDate);

        //redis refreshToken 저장
        refreshTokenService.removeRefreshToken(userId);
        refreshTokenService.saveTokenInfo(userId, refreshToken);

        return AuthToken.of(accessToken, refreshToken, BEARER_TYPE, ACCESS_TOKEN_EXPIRE_TIME);
    }
}
