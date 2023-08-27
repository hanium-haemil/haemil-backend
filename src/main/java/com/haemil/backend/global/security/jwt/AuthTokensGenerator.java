package com.haemil.backend.global.security.jwt;

import com.haemil.backend.auth.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthTokensGenerator {
    private static final String BEARER_TYPE = "Bearer";

    @Value("${jwt.access-period}")
    private long ATPeriod;

    @Value("${jwt.refresh-period}")
    private long RTPeriod;

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    // 토큰 발급
    public AuthTokens generate(String provider, Long userId, String email) {
        // RT가 이미 있을 경우
        if(redisService.getValues("RT(" + provider + "):" + email + "") != null) {
            redisService.deleteValues("RT(" + provider + "):" + email); // 삭제
        }

        Date now = new Date();

        Date accessTokenExpiredAt = new Date(now.getTime() + ATPeriod);
        Date refreshTokenExpiredAt = new Date(now.getTime() + RTPeriod);

        String subject = userId.toString();
        String accessToken = jwtTokenProvider.generate(email, subject, accessTokenExpiredAt);
        String refreshToken = jwtTokenProvider.generate(email, subject, refreshTokenExpiredAt);

        // Redis에 RT 저장
        saveRefreshToken(provider, email, refreshToken);
        return AuthTokens.of(accessToken, refreshToken, BEARER_TYPE, ATPeriod / 1000L);
    }

    public Long extractuserId(String accessToken) {
        return Long.valueOf(jwtTokenProvider.extractSubject(accessToken));
    }

    // 중복 메서드라 처리 필요. -> 합치는 방향 고안해볼 것.
    public void saveRefreshToken(String provider, String principal, String refreshToken) {
        redisService.setValuesWithTimeout("RT(" + provider + "):" + principal, // key
                refreshToken, // value
                jwtTokenProvider.getTokenExpirationTime(refreshToken)); // timeout(milliseconds)
    }
}
