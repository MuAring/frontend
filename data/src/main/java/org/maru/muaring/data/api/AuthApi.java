package org.maru.muaring.data.api;

import org.maru.muaring.data.api.dto.AuthorizeUrlResponse;
import org.maru.muaring.data.api.dto.KakaoLoginRequest;
import org.maru.muaring.data.api.dto.LoginResponse;
import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.SpotifyLoginRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AuthApi {

    @POST("/auth/login/kakao")
    Call<ApiResponse<LoginResponse>> loginWithKakao(@Body KakaoLoginRequest request);

    @POST("/auth/login/spotify")
    Call<ApiResponse<LoginResponse>> loginWithSpotify(@Body SpotifyLoginRequest request);

    @GET("/auth/spotify/authorization")
    Call<ApiResponse<AuthorizeUrlResponse>> getAuthorizedUrl();
}
