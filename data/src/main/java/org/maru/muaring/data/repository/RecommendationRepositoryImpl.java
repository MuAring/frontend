package org.maru.muaring.data.repository;

import androidx.annotation.NonNull;

import org.maru.muaring.core.common.Callback;
import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.RecommendationApi;
import org.maru.muaring.data.api.dto.GroupRecommendListResponseDto;
import org.maru.muaring.data.api.dto.MemberRecommendItemDto;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Response;

@Singleton
public class RecommendationRepositoryImpl implements RecommendationRepository {

    private final RecommendationApi api;

    @Inject
    public RecommendationRepositoryImpl(RecommendationApi api) {
        this.api = api;
    }

    // =========================
    // 그룹 추천
    // =========================

    @Override
    public void getGroupRecommendations(
            int limit,
            Callback<GroupRecommendListResponseDto> callback
    ) {
        api.getRecommendedGroups(limit)
                .enqueue(new retrofit2.Callback<ApiResponse<GroupRecommendListResponseDto>>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<ApiResponse<GroupRecommendListResponseDto>> call,
                            @NonNull Response<ApiResponse<GroupRecommendListResponseDto>> response
                    ) {
                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            callback.onSuccess(response.body().getData());

                        } else {
                            callback.onError(
                                    new Exception("그룹 추천 API 실패")
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<ApiResponse<GroupRecommendListResponseDto>> call,
                            @NonNull Throwable t
                    ) {
                        callback.onError(new Exception(t));
                    }
                });
    }

    @Override
    public void logGroupClick(long groupId) {
        api.logGroupClick(groupId).enqueue(new EmptyCallback<>());
    }

    @Override
    public void logGroupJoin(long groupId) {
        api.logGroupJoin(groupId).enqueue(new EmptyCallback<>());
    }

    // =========================
    // 멤버 추천
    // =========================

    @Override
    public void getMemberRecommendations(
            int limit,
            Callback<List<MemberRecommendItemDto>> callback
    ) {
        api.getRecommendedMembers(limit)
                .enqueue(new retrofit2.Callback<ApiResponse<List<MemberRecommendItemDto>>>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<ApiResponse<List<MemberRecommendItemDto>>> call,
                            @NonNull Response<ApiResponse<List<MemberRecommendItemDto>>> response
                    ) {
                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            callback.onSuccess(response.body().getData());

                        } else {
                            callback.onError(
                                    new Exception("멤버 추천 API 실패")
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<ApiResponse<List<MemberRecommendItemDto>>> call,
                            @NonNull Throwable t
                    ) {
                        callback.onError(new Exception(t));
                    }
                });
    }

    @Override
    public void logMemberClick(long targetMemberId) {
        api.logMemberClick(targetMemberId).enqueue(new EmptyCallback<>());
    }

    @Override
    public void logMemberFollow(long targetMemberId) {
        api.logMemberFollow(targetMemberId).enqueue(new EmptyCallback<>());
    }

    // =========================
    // 공통: 로그용 EmptyCallback
    // =========================
    private static class EmptyCallback<T>
            implements retrofit2.Callback<ApiResponse<T>> {

        @Override
        public void onResponse(
                @NonNull Call<ApiResponse<T>> call,
                @NonNull Response<ApiResponse<T>> response
        ) {
            // 로그 API → 성공/실패 무시
        }

        @Override
        public void onFailure(
                @NonNull Call<ApiResponse<T>> call,
                @NonNull Throwable t
        ) {
            // 로그 API → 실패 무시
        }
    }
}
