package com.haemil.backend.auth.service;


import com.haemil.backend.auth.dto.LoginDto;
import com.haemil.backend.auth.dto.RespLoginDto;
import com.haemil.backend.global.config.BaseException;
import com.haemil.backend.global.config.ResponseStatus;
import com.haemil.backend.global.security.jwt.AuthTokens;
import com.haemil.backend.global.security.jwt.AuthTokensGenerator;
import com.haemil.backend.global.security.jwt.JwtTokenProvider;
import com.haemil.backend.global.security.oauth.OAuthInfoResponse;
import com.haemil.backend.global.security.oauth.OAuthLoginParams;
import com.haemil.backend.global.security.oauth.RequestOAuthInfoService;
import com.haemil.backend.user.entity.User;
import com.haemil.backend.user.entity.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class AuthService {

    private final RedisService redisService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final AuthTokensGenerator authTokensGenerator;
    private final RequestOAuthInfoService requestOAuthInfoService;

    private final String SERVER = "Server";

    @Value("${jwt.cookie-period}")
    private long CookiePeriod;

    // 로그인: 인증 정보 저장 및 비어 토큰 발급
    public RespLoginDto login(OAuthLoginParams params) throws BaseException {

        OAuthInfoResponse oAuthInfoResponse = requestOAuthInfoService.request(params);
        Long userId = findOrCreateUser(oAuthInfoResponse);
        System.out.println("========> userId : " + userId);
        AuthTokens token = createToken(userId, oAuthInfoResponse);

        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException(ResponseStatus.NO_USER));
        LoginDto loginDto = getLoginDto(userId, user, token);

        HttpHeaders headers = new HttpHeaders();
        ResponseCookie httpCookie = saveHttpCookie(token);

        headers.add(HttpHeaders.SET_COOKIE, httpCookie.toString());
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache");
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token.getAccessToken());

        return new RespLoginDto(headers, loginDto);
    }

    // RT를 Redis에 저장
    @Transactional
    public void saveRefreshToken(String provider, String principal, String refreshToken) {
        redisService.setValuesWithTimeout("RT(" + provider + "):" + principal, // key
                refreshToken, // value
                jwtTokenProvider.getTokenExpirationTime(refreshToken)); // timeout(milliseconds)
    }

    // AT가 만료일자만 초과한 유효한 토큰인지 검사 - true일 경우 재발급이 필요.
    public boolean isValidateRequired(String requestAccessTokenInHeader) {
        String requestAccessToken = resolveToken(requestAccessTokenInHeader);
        try {
            Long userId = authTokensGenerator.extractuserId(requestAccessToken);
            if (!userRepository.existsById(userId)) {
                return true;
            }
        } catch (Exception e) {
            return true;
        }
        return jwtTokenProvider.validateAccessTokenOnlyExpired(requestAccessToken); // true = 재발급
    }

    // 토큰 재발급: validate 메서드가 true 반환할 때만 사용 -> AT 제거
    @Transactional
    public AuthTokens.TokenDto reissue(String requestAccessTokenInHeader, String requestRefreshToken) {
        String requestAccessToken = resolveToken(requestAccessTokenInHeader);

        Authentication authentication = jwtTokenProvider.getAuthentication(requestAccessToken);
        String subject = jwtTokenProvider.getMemId(requestAccessToken);

        String principal = getPrincipal(requestAccessToken);

        // 헤더로부터 RefreshToken 추출
        String refreshTokenInRedis = redisService.getValues("RT(" + SERVER + "):" + principal);
        String keyInRedis = ("RT(" + SERVER + "):" + principal);

        if (refreshTokenInRedis == null) { // Redis에 저장되어 있는 RT가 없을 경우
            return null; // -> 재로그인 요청
        }

        // 요청된 RT의 유효성 검사 & Redis에 저장되어 있는 RT와 같은지 비교
        if(!jwtTokenProvider.validateRefreshToken(requestRefreshToken, keyInRedis) || !refreshTokenInRedis.equals(requestRefreshToken)) {
            redisService.deleteValues("RT(" + SERVER + "):" + principal); // 탈취 가능성 -> 삭제
            log.info("RT의 탈취 가능성으로 삭제가 진행되었습니다. 다시 로그인해주세요.");

            return null; // -> 재로그인 요청
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 토큰 재발급 및 Redis 업데이트
        redisService.deleteValues("RT(" + SERVER + "):" + principal); // 기존 RT 삭제
//        AuthTokens.TokenDto tokenDto = AuthTokensGenerator.reissueGenerate(principal, authorities);

        // 토큰 재발급
        long now = (new Date()).getTime();
        Date accessTokenExpiredAt = new Date(now + 1000 * 60 * 30);
        Date refreshTokenExpiredAt = new Date(now + 1000 * 60 * 60 * 24 * 7);

        String accessToken = jwtTokenProvider.generate(principal, subject, accessTokenExpiredAt);
        String refreshToken = jwtTokenProvider.generate(principal, subject, refreshTokenExpiredAt);

        AuthTokens.TokenDto tokenDto = new AuthTokens.TokenDto(accessToken, refreshToken);

        saveRefreshToken(SERVER, principal, tokenDto.getRefreshToken());
        return tokenDto;
    }

    // 로그아웃
    @Transactional
    public void logout(String requestAccessTokenInHeader) throws BaseException {

        try {
            String requestAccessToken = resolveToken(requestAccessTokenInHeader);
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String principal = getPrincipal(requestAccessToken);

            log.info("AT input: {}", requestAccessToken);
            log.info("principal: {}", principal);

            // Redis에 저장되어 있는 RT 삭제
            String refreshTokenInRedis = redisService.getValues("RT(" + SERVER + "):" + principal);
            log.info("refreshTokenInRedis = {}", refreshTokenInRedis);
            if (refreshTokenInRedis == null) {
                throw new BaseException(ResponseStatus.INVALID_AUTH);
            } else {
                redisService.deleteValues("RT(" + SERVER + "):" + principal);
            }

            // Redis에 로그아웃 처리한 AT 저장
            long expiration = jwtTokenProvider.getTokenExpirationTime(requestAccessToken) - new Date().getTime();
            redisService.setValuesWithTimeout(requestAccessToken, "logout", expiration);

        } catch (IllegalArgumentException e) {
            throw new BaseException(ResponseStatus.INVALID_TOKEN);
        } catch (BaseException e) {
            throw new BaseException(ResponseStatus.INVALID_AUTH);
        }
    }

    /* -- 그 외 메서드 -- */

    // 토큰 생성 및 redis 저장
    public AuthTokens createToken(Long userId, OAuthInfoResponse oAuthInfoResponse) {
        AuthTokens token = authTokensGenerator.generate(SERVER, userId, oAuthInfoResponse.getEmail());
        return token;
    }

    // LoginDto GET 및 생성
    public LoginDto getLoginDto(Long userId,  User user, AuthTokens token) {
        String nickname = user.getNickname();
        String profileImageUrl = user.getProfileImageUrl();
        String AccessToken = token.getAccessToken();

        LoginDto loginDto = LoginDto.builder()
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .userId(userId)
                .accessToken(AccessToken)
                .build();

        return loginDto;
    }

    public ResponseCookie saveHttpCookie(AuthTokens token) {
        // RT 저장
        ResponseCookie httpCookie = ResponseCookie.from("refresh-token", token.getRefreshToken())
                .maxAge(CookiePeriod)
                .domain("todohaemil.com")
                .path("/")
                .secure(true)
                .httpOnly(true)
                .build();

        return httpCookie;
    }

    private Long findOrCreateUser(OAuthInfoResponse oAuthInfoResponse) {
        return userRepository.findByEmail(oAuthInfoResponse.getEmail())
                .map(User::getId)
                .orElseGet(() -> newUser(oAuthInfoResponse));
    }

    private Long newUser(OAuthInfoResponse oAuthInfoResponse) {
        log.debug("oAuthInfoResponse : {}", oAuthInfoResponse.getEmail());

        User user = User.builder()
                .email(oAuthInfoResponse.getEmail())
                .nickname(oAuthInfoResponse.getNickname())
                .profileImageUrl(oAuthInfoResponse.getProfileImageUrl())
                .oAuthProvider(oAuthInfoResponse.getOAuthProvider())
                .role(User.Role.USER) // 추가.
                .build();

        System.out.println("========> user : " + user);
        return userRepository.save(user).getId();
    }

    // 권한 이름 가져오기
    public String getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    // AT로부터 principal 추출
    public String getPrincipal(String requestAccessToken) {
        return jwtTokenProvider.getAuthentication(requestAccessToken).getName();
    }

    // "Bearer {AT}"에서 {AT} 추출
    public String resolveToken(String requestAccessTokenInHeader) {
        if (requestAccessTokenInHeader == null || !requestAccessTokenInHeader.startsWith("Bearer ")) {
            log.info("에러 발생!");
            throw new IllegalArgumentException("Invalid token in Authorization header");
        }
        String token = requestAccessTokenInHeader.substring(7);

        if (token.length() != 203)
            throw new IllegalArgumentException("Invalid token in Authorization header");

        return token;
    }
}
