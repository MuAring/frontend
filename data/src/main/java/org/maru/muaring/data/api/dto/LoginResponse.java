package org.maru.muaring.data.api.dto;

public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private String spotifyAccessToken;
    private Long memberId;
    private String email;
    private String nickname;
    private boolean hasNickname;

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getSpotifyAccessToken() {
        return spotifyAccessToken;
    }

    public long getMemberId() {
        return memberId;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public boolean hasNickname() {
        return hasNickname;
    }
}
