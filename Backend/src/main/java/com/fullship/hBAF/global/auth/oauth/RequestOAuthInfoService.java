package com.fullship.hBAF.global.auth.oauth;

import com.fullship.hBAF.global.auth.dto.loginRequest.OAuthLoginRequest;
import com.fullship.hBAF.global.auth.dto.memberInfo.OAuthMemberInfo;
import com.fullship.hBAF.domain.member.entity.OAuthProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RequestOAuthInfoService {
    private final Map<OAuthProvider, OAuthClient> clients;

    /**
     * 등록된 소셜로그인 api를 파악하고 clients를 생성
     * @param clients: 사용하는 소셜로그인 api들
     */
    public RequestOAuthInfoService(List<OAuthClient> clients) {
        this.clients = clients.stream().collect(
                Collectors.toUnmodifiableMap(OAuthClient::oauthProvider, Function.identity())
        );
    }

    /**
     * api를 통하여 userInfo 가져오는 메서드
     * @param request
     * @return
     */
    public OAuthMemberInfo request(OAuthLoginRequest request) {
        OAuthClient client = clients.get(request.oAuthProvider());
        String accessToken = client.requestAccessToken(request);
        return client.requestOAuthInfo(accessToken);
    }
}
