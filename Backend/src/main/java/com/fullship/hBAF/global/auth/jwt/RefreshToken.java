package com.fullship.hBAF.global.auth.jwt;

import lombok.Getter;

@Getter
public class RefreshToken {
    private String refreshToken;
    private String grantType;
    private long expiresIn;

    private RefreshToken(String refreshToken, String grantType , long expiresIn) {
        this.refreshToken = refreshToken;
        this.grantType = grantType;
        this.expiresIn = expiresIn;
    }

    /**
     * 유저전달 토큰 생성
     * @param refreshToken
     * @param expiresIn
     * @return
     */
    public static RefreshToken of(String refreshToken, String grantType, long expiresIn) {
        return new RefreshToken(refreshToken, grantType, expiresIn);
    }
}