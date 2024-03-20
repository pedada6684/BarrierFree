package com.fullship.hBAF.global.auth.service;

import com.fullship.hBAF.global.auth.entity.RedisRefreshToken;
import com.fullship.hBAF.global.auth.repository.RefreshTokenRepository;
import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRedisRepository;

    @Transactional(readOnly = false)
    public void saveTokenInfo(Long memberId, String refreshToken) {
        RedisRefreshToken redisRefreshToken = new RedisRefreshToken(String.valueOf(memberId), refreshToken);
        refreshTokenRedisRepository.save(redisRefreshToken);
    }

    @Transactional(readOnly = false)
    public void removeRefreshToken(Long memberId) {
        refreshTokenRedisRepository.findById(String.valueOf(memberId))
                .ifPresent(refreshToken -> refreshTokenRedisRepository.delete(refreshToken));
    }

    public String findTokenByMemberId(Long memberId){
        RedisRefreshToken redisRefreshToken = refreshTokenRedisRepository.findById(String.valueOf(memberId))
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));
        return redisRefreshToken.getRefreshToken();
    }
}
