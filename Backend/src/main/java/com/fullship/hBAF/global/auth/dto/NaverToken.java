package com.fullship.hBAF.global.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * naver 에서 사용하는 accessToken
 * https://developers.naver.com/docs/login/api/api.md#4-2--%EC%A0%91%EA%B7%BC-%ED%86%A0%ED%81%B0-%EB%B0%9C%EA%B8%89-%EC%9A%94%EC%B2%AD
 * @param accessToken
 * @param refreshToken
 * @param tokenType 접근 타입
 * @param expiresIn accessToken 유효기간 초단위
 */
public record NaverToken(
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("refresh_token")
        String refreshToken,
        @JsonProperty("token_type")
        String tokenType,
        @JsonProperty("expires_in")
        String expiresIn
) {
}