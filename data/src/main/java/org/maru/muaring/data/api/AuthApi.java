package org.maru.muaring.data.api;

import org.maru.muaring.data.api.dto.LoginRequest;
import org.maru.muaring.data.api.dto.LoginResponse;
import org.maru.muaring.data.api.dto.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {

    @POST("/auth/login/kakao")
    Call<ApiResponse<LoginResponse>> loginWithKakao(@Body LoginRequest request);
}
