package org.maru.muaring.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.GroupApi;
import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.GroupCategoryResponse;
import org.maru.muaring.data.api.dto.GroupCreateRequest;
import org.maru.muaring.data.api.dto.GroupCreateResponse;
import org.maru.muaring.data.api.dto.GroupInviteResponse;
import org.maru.muaring.data.api.dto.GroupListResponse;
import org.maru.muaring.data.api.dto.GroupSummary;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupRepositoryImpl implements GroupRepository {

    @Override
    public Call<ApiResponse<GroupCreateResponse>> createGroup(GroupCreateRequest request) {
        return groupApi.createGroup(request);
    }

    private static final String TAG = "GroupRepository";
    private final GroupApi groupApi;

    @Inject
    public GroupRepositoryImpl(GroupApi groupApi) {
        this.groupApi = groupApi;
    }

    @Override
    public LiveData<Resource<GroupInviteResponse>> createInviteLink(Long groupId) {
        MutableLiveData<Resource<GroupInviteResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Log.d(TAG, "초대 링크 생성 시작 - groupId: " + groupId);

        groupApi.createInviteLink(groupId).enqueue(new Callback<ApiResponse<GroupInviteResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<GroupInviteResponse>> call,
                                   Response<ApiResponse<GroupInviteResponse>> response) {

                Log.d(TAG, "응답 코드: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<GroupInviteResponse> apiResponse = response.body();
                    GroupInviteResponse data = apiResponse.getData();

                    if (data != null) {
                        Log.d(TAG, "초대 링크 생성 성공: " + data.getInviteUrl());
                        result.setValue(Resource.success(data));
                    } else {
                        Log.e(TAG, "데이터가 null입니다");
                        result.setValue(Resource.error("데이터를 받아오지 못했습니다", null));
                    }
                } else {
                    Log.e(TAG, "응답 실패: " + response.message());

                    String errorMessage;
                    switch (response.code()) {
                        case 401:
                            errorMessage = "로그인이 필요합니다";
                            break;
                        case 404:
                            errorMessage = "그룹을 찾을 수 없습니다";
                            break;
                        default:
                            errorMessage = "초대 링크 생성 실패 (코드: " + response.code() + ")";
                    }
                    result.setValue(Resource.error(errorMessage, null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<GroupInviteResponse>> call, Throwable t) {
                Log.e(TAG, "네트워크 오류", t);
                result.setValue(Resource.error("네트워크 오류: " + t.getMessage(), null));
            }
        });

        return result;
    }

    // 그룹 검색
    @Override
    public void searchGroups(String name, int page, int size, SearchGroupsCallback callback) {
        Call<ApiResponse<GroupListResponse>> call =
                groupApi.searchGroups(name, true, null, page, size);

        call.enqueue(new Callback<ApiResponse<GroupListResponse>>() {
            @Override
            public void onResponse(
                    @NonNull Call<ApiResponse<GroupListResponse>> call,
                    @NonNull Response<ApiResponse<GroupListResponse>> response
            ) {
                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<GroupListResponse> body = response.body();
                    GroupListResponse data = body.getData();

                    List<GroupSummary> groups =
                            (data != null && data.getGroups() != null)
                                    ? data.getGroups()
                                    : Collections.emptyList();

                    callback.onSuccess(groups);

                } else {
                    callback.onError(new RuntimeException("검색 실패: " + response.code()));
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<ApiResponse<GroupListResponse>> call,
                    @NonNull Throwable t
            ) {
                callback.onError(t);
            }
        });
    }


    // 그룹 카테고리 조회
    @Override
    public Call<ApiResponse<List<GroupCategoryResponse>>> getGroupCategories() {
        return groupApi.getGroupCategories();
    }

}