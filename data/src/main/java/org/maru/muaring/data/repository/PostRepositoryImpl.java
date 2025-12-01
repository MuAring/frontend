package org.maru.muaring.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.PostApi;
import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.MusicPostFeedResponse;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostRepositoryImpl implements PostRepository {

    private static final String TAG = "PostRepository";
    private final PostApi postApi;

    @Inject
    public PostRepositoryImpl(PostApi postApi) {
        this.postApi = postApi;
    }

    @Override
    public LiveData<Resource<List<MusicPostFeedResponse>>> getTodayPostsForMe() {
        MutableLiveData<Resource<List<MusicPostFeedResponse>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        postApi.getTodayPostsForMe().enqueue(new Callback<ApiResponse<List<MusicPostFeedResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<MusicPostFeedResponse>>> call,
                                   Response<ApiResponse<List<MusicPostFeedResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    result.setValue(Resource.error("오늘의 음악을 불러오지 못했어요.", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<MusicPostFeedResponse>>> call, Throwable t) {
                Log.e(TAG, "getTodayPostsForMe 실패", t);
                result.setValue(Resource.error("네트워크 오류가 발생했어요.", null));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<List<MusicPostFeedResponse>>> getTodayPostsForGroup(Long groupId) {
        MutableLiveData<Resource<List<MusicPostFeedResponse>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        postApi.getTodayPostsForGroup(groupId).enqueue(new Callback<ApiResponse<List<MusicPostFeedResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<MusicPostFeedResponse>>> call,
                                   Response<ApiResponse<List<MusicPostFeedResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    result.setValue(Resource.error("오늘의 음악을 불러오지 못했어요.", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<MusicPostFeedResponse>>> call, Throwable t) {
                Log.e(TAG, "getTodayPostsForGroup 실패", t);
                result.setValue(Resource.error("네트워크 오류가 발생했어요.", null));
            }
        });

        return result;
    }
}
