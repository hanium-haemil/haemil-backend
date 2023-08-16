package com.haemil.backend.auth.controller;

import com.haemil.backend.auth.dto.LoginDto;
import com.haemil.backend.auth.dto.RespLoginDto;
import com.haemil.backend.auth.dto.SuccessDto;
import com.haemil.backend.auth.dto.kakao.KakaoLoginParams;
import com.haemil.backend.auth.service.AuthService;
import com.haemil.backend.global.config.BaseException;
import com.haemil.backend.global.config.BaseResponse;
import com.haemil.backend.global.security.jwt.AuthTokens;
import com.haemil.backend.user.entity.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @Value("${jwt.cookie-period}")
    private long CookiePeriod;

    @PostMapping("/kakao")
    public ResponseEntity<BaseResponse> loginKakao(@RequestParam Boolean isGuardian, @RequestBody KakaoLoginParams params) {
        try {
            log.debug("isGuardian = {}", isGuardian);
            RespLoginDto respLoginDto = authService.login(params, isGuardian);

            HttpHeaders headers = respLoginDto.getHeaders();
            LoginDto loginDto = respLoginDto.getLoginDto();

            return ResponseEntity.ok().headers(headers).body(new BaseResponse<>(loginDto));
        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus()).convert();
        }
    }

    // 토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@CookieValue(name = "refresh-token") String requestRefreshToken,
                                     @RequestHeader("Authorization") String requestAccessToken) {

        AuthTokens.TokenDto reissuedTokenDto = authService.reissue(requestAccessToken, requestRefreshToken);

        SuccessDto successDto;
        if (reissuedTokenDto != null) { // 토큰 재발급 성공
            // RT 저장
            ResponseCookie responseCookie = ResponseCookie.from("refresh-token", reissuedTokenDto.getRefreshToken())
                    .maxAge(CookiePeriod)
                    .path("/")
                    .sameSite("None")
                    .httpOnly(true)
                    .secure(true)
                    .build();
            // success true
            successDto = SuccessDto.builder().success(true).build();
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    // AT 저장
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + reissuedTokenDto.getAccessToken())
                    .body(successDto);
//                    .build();

        } else { // Refresh Token 탈취 가능성
            // Cookie 삭제 후 재로그인 유도
            ResponseCookie responseCookie = ResponseCookie.from("refresh-token", "")
                    .maxAge(0)
                    .path("/")
                    .build();
            // success false
            successDto = SuccessDto.builder().success(false).build();

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    .body(successDto);
//                    .build();
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String requestAccessToken) {
        try {
            // RT가 null이 아니면서 empty가 아닌 경우 로그아웃 진행.
            if (requestAccessToken != null && !requestAccessToken.isEmpty())
                authService.logout(requestAccessToken);
            ResponseCookie responseCookie = ResponseCookie.from("refresh-token", "")
                    .maxAge(0)
                    .path("/")
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.SET_COOKIE, responseCookie.toString());

            return ResponseEntity.ok().headers(headers).body(new BaseResponse<>("로그아웃 되었습니다."));
        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus()).convert();
        }


    }

}
