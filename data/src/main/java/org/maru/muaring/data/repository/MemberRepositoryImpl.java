package org.maru.muaring.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.maru.muaring.core.common.Callback;
import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.MemberApi;
import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.MemberProfileCreateRequest;
import org.maru.muaring.data.api.dto.MemberProfileCreateResponse;
import org.maru.muaring.data.api.dto.MemberSettingsResponse;
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


}
