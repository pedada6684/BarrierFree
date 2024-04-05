package com.fullship.hBAF.global.auth.dto.memberInfo;

import com.fullship.hBAF.domain.member.entity.OAuthProvider;

public interface OAuthMemberInfo {
    String getEmail();
    String getNickname();
    String getName();

    OAuthProvider getOAuthProvider();
}
