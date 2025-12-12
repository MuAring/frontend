package org.maru.muaring.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.LikeApi;
import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.LikeResponseDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LikeRepositoryImpl implements LikeRepository {

    private static final String TAG = "LikeRepository";
    private final LikeApi likeApi;

    public LikeRepositoryImpl(LikeApi likeApi) {
        this.likeApi = likeApi;
    }

    @Override
    public LiveData<Resource<LikeResponseDTO>> toggleLike(Long postId) {
        MutableLiveData<Resource<LikeResponseDTO>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        likeApi.toggleLike(postId).enqueue(new Callback<ApiResponse<LikeResponseDTO>>() {
            @Override
            public void onResponse(Call<ApiResponse<LikeResponseDTO>> call,
                                   Response<ApiResponse<LikeResponseDTO>> response) {
                Log.d(TAG, "toggleLike onResponse code=" + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    result.setValue(Resource.error("좋아요 처리 실패 (HTTP " + response.code() + ")", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LikeResponseDTO>> call, Throwable t) {
                Log.e(TAG, "toggleLike onFailure", t);
                result.setValue(Resource.error("네트워크 오류: " + t.getMessage(), null));
            }
        });

        return result;
    }
}
