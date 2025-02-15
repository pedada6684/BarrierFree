package com.fullship.hBAF.global.auth.oauth;

import com.fullship.hBAF.global.auth.controller.request.OAuthLoginRequest;
import com.fullship.hBAF.global.auth.dto.memberInfo.OAuthMemberInfo;
import com.fullship.hBAF.domain.member.entity.OAuthProvider;

/**
 * Oauth 요청 clinet class
 */
public interface OAuthClient {
    OAuthProvider oauthProvider();

    /**
     * authorization_code 기반으로 access token받는 메서드
     * @param params : authorization_code, state
     * @return
     */
    String requestAccessToken(OAuthLoginRequest params);

    /**
     * access token를 사용하여 userInfo 가져오는 메서드
     * @param accessToken
     * @return
     */
    OAuthMemberInfo requestOAuthInfo(String accessToken);
}
