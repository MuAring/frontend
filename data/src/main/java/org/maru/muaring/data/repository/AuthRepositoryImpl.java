package org.maru.muaring.data.repository;

import org.maru.muaring.core.common.Callback;
import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.LoginRequest;
import org.maru.muaring.data.api.dto.KakaoLoginRequest;
import org.maru.muaring.data.api.dto.LoginResponse;
import org.maru.muaring.data.api.AuthApi;
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

        Response<ApiResponse<LoginResponse>> response
                = api.loginWithKakao(request).execute();  // 동기 호출
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

        if (response.isSuccessful() && response.body().getData() != null) {
            return response.body().getData();
        } else {
            throw new Exception("로그인 실패");
        }
    }
}
