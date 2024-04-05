package com.fullship.hBAF.global.auth.service;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenService {
    @Resource(name = "redisTemplateForToken")
    private HashOperations<String, Long, String> hashOperations;
    private final String key = "RefreshToken";

    public void saveTokenInfo(Long memberId, String refreshToken) {
        hashOperations.put(key, memberId, refreshToken);
    }

    public void removeRefreshToken(Long memberId) {
        if (hashOperations.hasKey(key, memberId)){
            hashOperations.delete(key, memberId);
        }
    }

    public String findTokenByMemberId(Long memberId){
        if (existTokenByMemberId(memberId)){
            return hashOperations.get(key, memberId);
        }else{
            return null;
        }
    }

    public boolean existTokenByMemberId(Long memberId){
        return hashOperations.hasKey(key, memberId);
    }


}
