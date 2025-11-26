package org.maru.muaring.data.api.dto;

public class KakaoLoginRequest {

    private String kakaoAccessToken;

    public KakaoLoginRequest(String accessToken) {
        this.kakaoAccessToken = accessToken;
    }
}