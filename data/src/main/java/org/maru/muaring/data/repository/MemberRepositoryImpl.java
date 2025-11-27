package org.maru.muaring.data.repository;

import org.maru.muaring.core.common.Callback;
import org.maru.muaring.data.api.MemberApi;
import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.MemberProfileCreateRequest;
import org.maru.muaring.data.api.dto.MemberProfileCreateResponse;
import org.maru.muaring.data.api.dto.NicknameCheckResponse;
import retrofit2.Call;
import retrofit2.Response;

public class MemberRepositoryImpl implements MemberRepository {

    private final MemberApi api;

    public MemberRepositoryImpl(MemberApi api) {
        this.api = api;
    }

    @Override
    public void checkNickname(String nickname, Callback<NicknameCheckResponse> callback) {
        api.checkNicknameDuplicated(nickname).enqueue(new retrofit2.Callback<>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<NicknameCheckResponse>> call,
                    Response<ApiResponse<NicknameCheckResponse>> response
            ) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(new Exception("API 응답을 가져오던 중 문제가 발생했습니다."));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<NicknameCheckResponse>> call, Throwable t) {
                callback.onError(new Exception(t.getMessage() + " 네트워크 오류가 발생했습니다."));
            }
        });
    }

    @Override
    public void createProfile(MemberProfileCreateRequest request, Callback<MemberProfileCreateResponse> callback) {
        api.createProfile(request).enqueue(new retrofit2.Callback<>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<MemberProfileCreateResponse>> call,
                    Response<ApiResponse<MemberProfileCreateResponse>> response
            ) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(new Exception("API 응답을 가져오던 중 문제가 발생했습니다."));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<MemberProfileCreateResponse>> call, Throwable t) {
                callback.onError(new Exception(t.getMessage() + " 네트워크 오류가 발생했습니다."));
            }
        });
    }
}
