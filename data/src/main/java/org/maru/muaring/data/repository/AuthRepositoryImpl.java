package org.maru.muaring.data.repository;

import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.LoginRequest;
import org.maru.muaring.data.api.dto.LoginResponse;
import org.maru.muaring.data.api.AuthApi;
import retrofit2.Response;

public class AuthRepositoryImpl implements AuthRepository {

    private final AuthApi api;

    public AuthRepositoryImpl(AuthApi api) {
        this.api = api;
    }
    @Override
    public LoginResponse loginWithKakao(String kakaoAccessToken) throws Exception {
        LoginRequest request = new LoginRequest(kakaoAccessToken);

        Response<ApiResponse<LoginResponse>> response
                = api.loginWithKakao(request).execute();  // 동기 호출

        if (response.isSuccessful() && response.body().getData() != null) {
            return response.body().getData();
        } else {
            throw new Exception("로그인 실패");
        }
    }
}
