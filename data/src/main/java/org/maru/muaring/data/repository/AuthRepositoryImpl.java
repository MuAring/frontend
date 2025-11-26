package org.maru.muaring.data.repository;

import org.maru.muaring.core.common.Callback;
import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.AuthorizeUrlResponse;
import org.maru.muaring.data.api.dto.KakaoLoginRequest;
import org.maru.muaring.data.api.dto.LoginResponse;
import org.maru.muaring.data.api.AuthApi;
import org.maru.muaring.data.api.dto.SpotifyLoginRequest;
import retrofit2.Call;
import retrofit2.Response;

public class AuthRepositoryImpl implements AuthRepository {

    private final AuthApi api;

    public AuthRepositoryImpl(AuthApi api) {
        this.api = api;
    }

    @Override
    public void loginWithKakao(String kakaoAccessToken, Callback<LoginResponse> callback) {
        KakaoLoginRequest request = new KakaoLoginRequest(kakaoAccessToken);

        // enqueue()를 사용해 비동기 요청 시작 (UI 스레드를 막지 않아 앱이 멈추지 않도록 함)
        api.loginWithKakao(request).enqueue(new retrofit2.Callback<>() {

            // 통신 성공 (백그라운드 스레드)
            @Override
            public void onResponse(
                    Call<ApiResponse<LoginResponse>> call,
                    Response<ApiResponse<LoginResponse>> response
            ) {

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {  // 성공
                    LoginResponse data = response.body().getData();
                    callback.onSuccess(data);

                } else {  // 에러
                    callback.onError(new Exception(response.code() + " 오류가 발생했습니다."));
                }
            }

            // 통신 실패 (네트워크 연결, 타임아웃 등)
            @Override
            public void onFailure(
                    Call<ApiResponse<LoginResponse>> call,
                    Throwable t
            ) {
                // 네트워크 통신 오류
                callback.onError(new Exception(t.getMessage() + " 네트워크 오류가 발생했습니다.")); // 콜백 실패 호출
            }
        });
    }

    @Override
    public void getSpotifyAuthorizeUrl(Callback<String> callback) {
        api.getAuthorizedUrl().enqueue(new retrofit2.Callback<>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<AuthorizeUrlResponse>> call,
                    Response<ApiResponse<AuthorizeUrlResponse>> response
            ) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError(new Exception(
                            response.code() + ": Spotify authorize URL 요청 중 오류가 발생했습니다."
                    ));
                    return;
                }

                ApiResponse<AuthorizeUrlResponse> apiRes = response.body();

                if (apiRes.getData() == null || apiRes.getData().getAuthorizeUrl() == null) {
                    callback.onError(new Exception("서버 응답에 authorize URL이 없습니다."));
                    return;
                }

                callback.onSuccess(apiRes.getData().getAuthorizeUrl());
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthorizeUrlResponse>> call, Throwable t) {
                callback.onError(new Exception(t.getMessage() + "네트워크 오류가 발생했습니다."));
            }
        });
    }

    @Override
    public void loginWithSpotifyCode(String spotifyCode, Callback<LoginResponse> callback) {
        SpotifyLoginRequest request = new SpotifyLoginRequest(spotifyCode);
        api.loginWithSpotify(request).enqueue(new retrofit2.Callback<>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse>> call,
                                   Response<ApiResponse<LoginResponse>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError(new Exception(
                            response.code() + ": 스포티파이 로그인 중 오류가 발생했습니다."
                    ));
                    return;
                }

                ApiResponse<LoginResponse> apiRes = response.body();

                if (apiRes.getData() == null) {
                    callback.onError(new Exception("서버 응답에 데이터가 없습니다."));
                    return;
                }

                callback.onSuccess(apiRes.getData());
            }

            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                callback.onError(new Exception(t));
            }
        });
    }

}