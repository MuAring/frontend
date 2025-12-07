package org.maru.muaring.data.repository;

import org.maru.muaring.core.common.Callback;
import org.maru.muaring.data.api.UploadApi;
import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.MusicPostRequest;
import org.maru.muaring.data.api.dto.MyGroupListResponse;
import org.maru.muaring.data.api.dto.SpotifyTrackResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class UploadRepositoryImpl implements UploadRepository {

    private final UploadApi uploadApi;

    public UploadRepositoryImpl(UploadApi uploadApi){
        this.uploadApi = uploadApi;
    }

    @Override
    public void searchMusic(String query, Callback<List<SpotifyTrackResponse>> callback) {

        uploadApi.searchMusic(query).enqueue(new retrofit2.Callback<>() {

            @Override
            public void onResponse(Call<ApiResponse<List<SpotifyTrackResponse>>> call,
                                   Response<ApiResponse<List<SpotifyTrackResponse>>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<List<SpotifyTrackResponse>> api = response.body();

                    if (api.isSuccess()) {
                        callback.onSuccess(api.getData());
                    } else {
                        callback.onError(new Exception(api.getMessage()));
                    }
                } else {
                    callback.onError(new Exception("서버 응답 오류: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<SpotifyTrackResponse>>> call, Throwable t) {
                callback.onError(new Exception("네트워크 오류: " + t.getMessage()));
            }
        });
    }

    @Override
    public void getMyGroups(Callback<MyGroupListResponse> callback) {
        uploadApi.getMyGroups(null).enqueue(new retrofit2.Callback<>() {
            @Override
            public void onResponse(Call<ApiResponse<MyGroupListResponse>> call,
                                   Response<ApiResponse<MyGroupListResponse>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<MyGroupListResponse> api = response.body();

                    if (api.isSuccess()) {
                        callback.onSuccess(api.getData());
                    } else {
                        callback.onError(new Exception(api.getMessage()));
                    }
                } else {
                    callback.onError(new Exception("서버 응답 오류: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<MyGroupListResponse>> call, Throwable t) {
                callback.onError(new Exception("네트워크 오류: " + t.getMessage()));
            }
        });
    }

    @Override
    public void createMusicPost(MusicPostRequest request, Callback<Void> callback) {
        uploadApi.createMusicPost(request).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError(new Exception("포스트 등록 실패"));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(new Exception(t));
            }
        });
    }

}
