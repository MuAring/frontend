package org.maru.muaring.data.repository;

import org.maru.muaring.core.common.Callback;
import org.maru.muaring.data.api.dto.LoginResponse;

public interface AuthRepository {

    void loginWithKakao(String kakaoToken, Callback<LoginResponse> callback);

    void getSpotifyAuthorizeUrl(Callback<String> callback);

    void loginWithSpotifyCode(String spotifyCode, Callback<LoginResponse> callback);
}
