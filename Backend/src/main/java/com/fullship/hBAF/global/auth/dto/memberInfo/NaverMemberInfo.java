package com.fullship.hBAF.global.auth.dto.memberInfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fullship.hBAF.domain.member.entity.OAuthProvider;
import lombok.Getter;


/**
 * 유저 정보 디테일 response
 * https://developers.naver.com/docs/login/profile/profile.md
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverMemberInfo implements OAuthMemberInfo {

    @JsonProperty(value = "response")
    private Response response;

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Response(String email, String name, String nickname) {
    }

    @Override
    public String getEmail() {
        return response.email;
    }

    @Override
    public String getName() {
        return response.name;
    }

    @Override
    public String getNickname() {
        return response.nickname;
    }

    @Override
    public OAuthProvider getOAuthProvider() {
        return OAuthProvider.NAVER;
    }
}
