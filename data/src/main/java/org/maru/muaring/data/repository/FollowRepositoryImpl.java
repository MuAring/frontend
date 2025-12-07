package org.maru.muaring.data.repository;

import org.maru.muaring.core.common.Callback;
import org.maru.muaring.data.api.FollowApi;
import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.FollowResponseDTO;

import retrofit2.Call;
import retrofit2.Response;

public class FollowRepositoryImpl implements FollowRepository {

    private final FollowApi api;

    public FollowRepositoryImpl(FollowApi api) {
        this.api = api;
    }

    @Override
    public void followMember(long memberId, Callback<Void> callback) {

        api.followMember(memberId)
                .enqueue(new retrofit2.Callback<ApiResponse<FollowResponseDTO>>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse<FollowResponseDTO>> call,
                            Response<ApiResponse<FollowResponseDTO>> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(null); // 성공 시 데이터 필요 없음
                        } else {
                            callback.onError(new Exception("팔로우 요청 실패"));
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<FollowResponseDTO>> call,
                            Throwable t
                    ) {
                        callback.onError(new Exception("네트워크 오류", t));
                    }
                });
    }

    @Override
    public void unfollowMember(long memberId, Callback<Void> callback) {

        api.unfollowMember(memberId)
                .enqueue(new retrofit2.Callback<ApiResponse<Void>>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse<Void>> call,
                            Response<ApiResponse<Void>> response
                    ) {
                        if (response.isSuccessful()) {
                            callback.onSuccess(null);
                        } else {
                            callback.onError(new Exception("언팔로우 실패"));
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<Void>> call,
                            Throwable t
                    ) {
                        callback.onError(new Exception("네트워크 오류", t));
                    }
                });
    }
}
