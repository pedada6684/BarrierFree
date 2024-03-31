package com.fullship.hBAF.global.auth.jwt;

import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;
    private static final String BEARER_TYPE = "Bearer";


    /**
     * jwt parsing key 설정
     * @param secretKey: env key
     */
    public JwtTokenProvider(@Value("${jwt.secret-key}") String secretKey){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * jwt 발행 메서드
     * @param subject: userId
     * @param expiredDate
     * @return jwt토큰
     */
    public String generate(String subject, Date expiredDate){
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(expiredDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * jwt 유효성 검증 메서드
     * @param token jwt 토큰
     * @return
     */
    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {//유효하지 않은 토큰
            log.warn("Invalid JWT Token", e);
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        } catch (ExpiredJwtException e) {//만료된 토큰
            log.warn("Expired JWT Token", e);
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {//잘못된 서명
            log.warn("Unsupported JWT Token", e);
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        } catch (IllegalArgumentException e) {//빈 클레임
            log.warn("JWT claims string is empty.", e);
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    /**
     * jwt 유효성 검증 메서드
     * @param token jwt 토큰
     * @return
     */
    public boolean validateAccessToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {//유효하지 않은 토큰
            log.warn("Invalid JWT Token", e);
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }  catch (UnsupportedJwtException e) {//잘못된 서명
            log.warn("Unsupported JWT Token", e);
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        } catch (IllegalArgumentException e) {//빈 클레임
            log.warn("JWT claims string is empty.", e);
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }
    /**
     * userId 추출 메서드
     * @param accessToken: 전달받은 jwt 토큰
     * @return
     */
    public String extractSubject(String accessToken) {
        Claims claims = parseClaims(accessToken);
        return claims.getSubject();
    }

    /**
     * decoding 메서드
     * @param accessToken
     * @return
     */
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String resolveToken(String bearerToken){
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)){
            return bearerToken.substring(7);
        }
        return null;
    }
}
