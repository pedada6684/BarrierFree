package com.fullship.hBAF.global.auth.oauth;

import com.fullship.hBAF.global.auth.dto.NaverToken;
import com.fullship.hBAF.global.auth.dto.loginRequest.OAuthLoginRequest;
import com.fullship.hBAF.global.auth.dto.memberInfo.NaverMemberInfo;
import com.fullship.hBAF.global.auth.dto.memberInfo.OAuthMemberInfo;
import com.fullship.hBAF.domain.member.entity.OAuthProvider;
import com.fullship.hBAF.global.api.service.ApiService;
import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class NaverApiClient implements  OAuthClient{
    @Value("${oauth.naver.url.auth}")
    private String authUrl;
    @Value("${oauth.naver.url.api}")
    private String apiUrl;
    @Value("${oauth.naver.client-id}")
    private String clientId;
    @Value("${oauth.naver.client-secret}")
    private String clientSecret;

    private final ApiService<NaverToken> naverTokenApiService;
    private final ApiService<NaverMemberInfo> naverUserInfoApiService;

    @Override
    public OAuthProvider oauthProvider() {
        return OAuthProvider.NAVER;
    }

    @Override
    public String requestAccessToken(OAuthLoginRequest request) {
        HttpHeaders headers = setHttpHeaders();
        MultiValueMap<String, String> body = request.makeBody();
        body.add("grant_type", OAuthConstant.GRANT_TYPE);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        UriComponents uri = UriComponentsBuilder
                .fromHttpUrl(authUrl + "/oauth2.0/token")
                .build();

        ResponseEntity<NaverToken> response = naverTokenApiService.post(uri.toUri(), headers, body, NaverToken.class);
        if (response.getStatusCode().is2xxSuccessful()){
            NaverToken naverToken = response.getBody();
            Objects.requireNonNull(naverToken);
            return naverToken.accessToken();
        }else {
            log.error("uri: "+ uri.toString());
            log.error("body: "+ body);
            log.error("response.getStatusCode(): "+ response.getStatusCode());
            throw new CustomException(ErrorCode.NO_AVAILABLE_API);
        }
    }

    @Override
    public OAuthMemberInfo requestOAuthInfo(String accessToken) {
        HttpHeaders headers = setHttpHeaders(accessToken);
        UriComponents uri = UriComponentsBuilder
                .fromHttpUrl(apiUrl + "/v1/nid/me")
                .build();
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        ResponseEntity<NaverMemberInfo> response = naverUserInfoApiService.post(uri.toUri(), headers, body, NaverMemberInfo.class);
        if (response.getStatusCode().is2xxSuccessful()){
            NaverMemberInfo naverMemberInfo = response.getBody();
            Objects.requireNonNull(naverMemberInfo);
            return naverMemberInfo;
        }else {
            log.error("uri: "+ uri.toString());
            log.error("body: "+ body);
            log.error("response.getStatusCode(): "+ response.getStatusCode());
            throw new CustomException(ErrorCode.NO_AVAILABLE_API);
        }
    }
    private HttpHeaders setHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        // 접근 토큰 갱신 / 삭제 요청시 access_token 값은 URL 인코딩하셔야 합니다.
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }
    private HttpHeaders setHttpHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        // 접근 토큰 갱신 / 삭제 요청시 access_token 값은 URL 인코딩하셔야 합니다.
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + accessToken);
        return headers;
    }
}