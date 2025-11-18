package org.maru.muaring.data.api.dto;

public class LoginRequest {

    private String kakaoAccessToken;

    public LoginRequest(String accessToken) {
        this.kakaoAccessToken = accessToken;
    }
}