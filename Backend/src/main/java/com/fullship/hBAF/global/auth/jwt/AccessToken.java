package com.fullship.hBAF.global.auth.jwt;

import lombok.Getter;

@Getter
public class AccessToken {
    private String accessToken;
    private String grantType;
    private long expiresIn;

    private AccessToken(String accessToken, String grantType, long expiresIn) {
        this.accessToken = accessToken;
        this.grantType = grantType;
        this.expiresIn = expiresIn;
    }

    /**
     * 유저전달 토큰 생성
     * @param accessToken
     * @param grantType
     * @param expiresIn
     * @return
     */
    public static AccessToken of(String accessToken, String grantType, long expiresIn) {
        return new AccessToken(accessToken, grantType, expiresIn);
    }
}
