package com.fullship.hBAF.global.auth.dto.loginRequest;

import com.fullship.hBAF.domain.member.entity.OAuthProvider;
import org.springframework.util.MultiValueMap;

public interface OAuthLoginRequest {
    OAuthProvider oAuthProvider();
    MultiValueMap<String, String> makeBody();
}