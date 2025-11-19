package org.maru.muaring.data.api.dto;

public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private long memberId;
    private String email;
    private boolean hasNickname;

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public long getMemberId() {
        return memberId;
    }

    public String getEmail() {
        return email;
    }

    public boolean hasNickname() {
        return hasNickname;
    }
}
