package org.maru.muaring.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.maru.muaring.core.common.Callback;
import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.MemberApi;
import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.MemberProfileCreateRequest;
import org.maru.muaring.data.api.dto.MemberProfileCreateResponse;
import org.maru.muaring.data.api.dto.MemberProfileReadResponse;
import org.maru.muaring.data.api.dto.MemberSearchItemDto;
import org.maru.muaring.data.api.dto.MemberSettingsResponse;
import org.maru.muaring.data.api.dto.MemberProfileSettingReadResponse;
import org.maru.muaring.data.api.dto.MemberProfileUpdateRequest;
import org.maru.muaring.data.api.dto.NicknameCheckResponse;
import org.maru.muaring.data.api.dto.PageResponse;

import java.util.List;

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

    @Override
    public LiveData<Resource<MemberSettingsResponse>> getMySettings() {
        MutableLiveData<Resource<MemberSettingsResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        api.getMySettings().enqueue(new retrofit2.Callback<ApiResponse<MemberSettingsResponse>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<MemberSettingsResponse>> call,
                    Response<ApiResponse<MemberSettingsResponse>> response
            ) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    result.setValue(Resource.error("내 프로필 정보를 불러오지 못했어요.", null));
                }
            }

            @Override
            public void onFailure(
                    Call<ApiResponse<MemberSettingsResponse>> call,
                    Throwable t
            ) {
                result.setValue(Resource.error("네트워크 오류가 발생했어요.", null));
            }
        });

        return result;
    }



    @Override
    public void loadProfile(Callback<MemberProfileSettingReadResponse> callback) {
        api.loadProfile().enqueue(new retrofit2.Callback<>() {

            @Override
            public void onResponse(
                    Call<ApiResponse<MemberProfileSettingReadResponse>> call,
                    Response<ApiResponse<MemberProfileSettingReadResponse>> response
            ) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(new Exception("API 응답을 가져오던 중 문제가 발생했습니다."));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<MemberProfileSettingReadResponse>> call, Throwable t) {
                callback.onError(new Exception(t.getMessage() + " 네트워크 오류가 발생했습니다."));
            }
        });
    }

    @Override
    public void updateProfile(MemberProfileUpdateRequest request, Callback<Void> callback) {
        api.updateProfile(request).enqueue(new retrofit2.Callback<>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(new Exception("API 응답을 가져오던 중 문제가 발생했습니다."));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onError(new Exception(t.getMessage() + " 네트워크 오류가 발생했습니다."));
            }
        });
    }

    @Override
    public void searchMembers(
            String name,
            int page,
            int size,
            SearchMembersCallback callback
    ) {
        api.searchMembers(name, page, size)
                .enqueue(new retrofit2.Callback<ApiResponse<PageResponse<MemberSearchItemDto>>>() {
                    @Override
                    public void onResponse(
                            Call<ApiResponse<PageResponse<MemberSearchItemDto>>> call,
                            Response<ApiResponse<PageResponse<MemberSearchItemDto>>> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {

                            ApiResponse<PageResponse<MemberSearchItemDto>> body = response.body();
                            PageResponse<MemberSearchItemDto> pageResponse = body.getData();

                            // 로그 찍어서 실제로 몇 개 오는지 확인
                            int count = 0;
                            if (pageResponse != null && pageResponse.getContent() != null) {
                                count = pageResponse.getContent().size();
                            }
                            Log.d("MemberRepository", "searchMembers success, count = " + count);

                            List<MemberSearchItemDto> content =
                                    (pageResponse != null && pageResponse.getContent() != null)
                                            ? pageResponse.getContent()
                                            : java.util.Collections.emptyList();

                            callback.onSuccess(content);

                        } else {
                            callback.onError(new Exception("멤버 검색에 실패했어요."));
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<PageResponse<MemberSearchItemDto>>> call,
                            Throwable t
                    ) {
                        callback.onError(t);
                    }
                });
    }

    @Override
    public LiveData<Resource<MemberProfileReadResponse>> getMemberProfile(Long memberId) {
        MutableLiveData<Resource<MemberProfileReadResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        api.readMemberProfile(memberId).enqueue(new retrofit2.Callback<ApiResponse<MemberProfileReadResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<MemberProfileReadResponse>> call,
                                   Response<ApiResponse<MemberProfileReadResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<MemberProfileReadResponse> body = response.body();
                    if (body.getData() != null) {
                        result.setValue(Resource.success(body.getData()));
                    } else {
                        result.setValue(Resource.error("data가 null입니다.", null));
                    }
                } else {
                    String msg = "response 실패 - code: " + response.code();
                    result.setValue(Resource.error(msg, null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<MemberProfileReadResponse>> call, Throwable t) {
                result.setValue(Resource.error("네트워크 오류: " + t.getMessage(), null));
            }
        });

        return result;
    }
}