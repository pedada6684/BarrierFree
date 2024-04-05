package com.fullship.hBAF.global.auth.jwt;

import lombok.Getter;

@Getter
public class AuthToken {
    private String accessToken;
    private String refreshToken;
    private String grantType;
    private Long expiresIn;

    private AuthToken(String accessToken, String refreshToken, String grantType, Long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.grantType = grantType;
        this.expiresIn = expiresIn;
    }

    /**
     * 유저전달 토큰 생성
     * @param accessToken
     * @param refreshToken
     * @param grantType
     * @param expiresIn
     * @return
     */
    public static AuthToken of(String accessToken, String refreshToken, String grantType, Long expiresIn) {
        return new AuthToken(accessToken, refreshToken, grantType, expiresIn);
    }
}
