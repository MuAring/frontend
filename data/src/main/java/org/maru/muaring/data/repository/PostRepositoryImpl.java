package org.maru.muaring.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.PostApi;
import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.MusicPostFeedResponse;
import org.maru.muaring.data.api.dto.PageResponse;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostRepositoryImpl implements PostRepository {

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
                    result.setValue(Resource.error("오늘의 음악 조회 실패", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<MusicPostFeedResponse>>> call, Throwable t) {
                t.printStackTrace();
                result.setValue(Resource.error("네트워크 오류: " + t.getMessage(), null));
            }
        });

        return result;
    }

    @Override
    public LiveData<Resource<List<MusicPostFeedResponse>>> getTodayPostsForGroup(Long groupId) {
        Log.d("PostRepositoryImpl", "getTodayPostsForGroup called with groupId: " + groupId);
        MutableLiveData<Resource<List<MusicPostFeedResponse>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        postApi.getTodayPostsForGroup(groupId)
                .enqueue(new Callback<ApiResponse<PageResponse<MusicPostFeedResponse>>>() {
                    @Override
                    public void onResponse(
                            Call<ApiResponse<PageResponse<MusicPostFeedResponse>>> call,
                            Response<ApiResponse<PageResponse<MusicPostFeedResponse>>> response
                    ) {
                        Log.d("PostRepositoryImpl",
                                "onResponse called, httpCode=" + response.code());

                        if (response.isSuccessful() && response.body() != null) {
                            PageResponse<MusicPostFeedResponse> page = response.body().getData();

                            List<MusicPostFeedResponse> list;
                            if (page != null && page.getContent() != null) {
                                list = page.getContent();
                            } else {
                                list = Collections.emptyList();
                            }

                            Log.d("PostRepositoryImpl",
                                    "SUCCESS, list size = " + list.size());
                            result.setValue(Resource.success(list));
                        } else {
                            Log.e("PostRepositoryImpl",
                                    "getTodayPostsForGroup 실패, code=" + response.code());
                            result.setValue(Resource.error("그룹 오늘의 피드 조회 실패", null));
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<PageResponse<MusicPostFeedResponse>>> call,
                            Throwable t
                    ) {
                        Log.e("PostRepositoryImpl",
                                "onFailure: " + t.getClass().getSimpleName() + " / " + t.getMessage(),
                                t);
                        result.setValue(Resource.error("네트워크 오류: " + t.getMessage(), null));
                    }
                });

        return result;
    }

}