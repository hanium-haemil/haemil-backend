package com.haemil.backend.user;


import com.haemil.backend.auth.dto.SuccessDto;
import com.haemil.backend.auth.service.AuthService;
import com.haemil.backend.global.config.BaseResponse;
import com.haemil.backend.global.security.jwt.AuthTokensGenerator;
import com.haemil.backend.user.entity.User;
import com.haemil.backend.user.entity.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final AuthTokensGenerator authTokensGenerator;

    @GetMapping
    public ResponseEntity<BaseResponse<List<User>>> findAll() {
        return ResponseEntity.ok().body(new BaseResponse<>(userRepository.findAll()));
    }

    @GetMapping("/validate")
    public ResponseEntity<SuccessDto> validate(@RequestHeader("Authorization") String requestAccessToken) {
        SuccessDto successDto;

        if (!authService.isValidateRequired(requestAccessToken)) {
            successDto = SuccessDto.builder().success(true).build();
            return ResponseEntity.status(HttpStatus.OK).body(successDto); // 재발급 필요X
        } else {
            successDto = SuccessDto.builder().success(false).build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(successDto); // 재발급 필요
        }
    }
}
