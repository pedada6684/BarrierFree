package com.fullship.hBAF.config.auth.memberInfo;

import com.fullship.hBAF.domain.member.entity.OAuthProvider;

public interface OAuthMemberInfo {
    String getEmail();
    String getNickname();
    String getName();

    OAuthProvider getOAuthProvider();
}
