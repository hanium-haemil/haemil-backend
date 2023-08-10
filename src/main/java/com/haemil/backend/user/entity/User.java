package com.haemil.backend.user.entity;

import com.haemil.backend.global.security.oauth.OAuthProvider;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Slf4j
@Getter
@Entity
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 20)
    private String nickname;

    // 임시 credential
    private String password;

    //role 추가.
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING) //저장될때는 string으로 저장되도록
    private Role role;

    // 프로필 이미지 url 추가.
    @Column
    private String profileImageUrl;

    private OAuthProvider oAuthProvider;

    @Getter
    @RequiredArgsConstructor
    public enum Role {
        USER("ROLE_USER"), ADMIN("ROLE_ADMIN");

        private final String key;
    }

//    public enum OAuthProvider {
//        KAKAO, GOOGLE
//    }

    @Builder
    public User(String email, String nickname, String profileImageUrl, Role role, OAuthProvider oAuthProvider) {
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
        this.oAuthProvider = oAuthProvider;
    }

    public User update(String name, String picture){
        this.nickname = name;
        this.profileImageUrl = picture;

        return this;
    }

    public String getRoleKey(){
        return this.role.getKey();
    }

}