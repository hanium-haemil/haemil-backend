package com.haemil.backend.global.security.oauth;

public interface OAuthInfoResponse {
    String getEmail();
    String getNickname();
    String getProfileImageUrl();
    OAuthProvider getOAuthProvider();
}
