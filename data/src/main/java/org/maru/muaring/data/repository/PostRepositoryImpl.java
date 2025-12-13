package org.maru.muaring.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.PostApi;
import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.MusicPostFeedResponse;
import org.maru.muaring.data.api.dto.PageResponse;
import org.maru.muaring.data.api.dto.TodayPostResponse;

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
        Log.d("PostRepositoryImpl", "getTodayPostsForMe called");
        MutableLiveData<Resource<List<MusicPostFeedResponse>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        postApi.getTodayPostsForMe()
                .enqueue(new Callback<ApiResponse<PageResponse<MusicPostFeedResponse>>>() {
                    @Override
                    public void onResponse(
                            Call<ApiResponse<PageResponse<MusicPostFeedResponse>>> call,
                            Response<ApiResponse<PageResponse<MusicPostFeedResponse>>> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {

                            PageResponse<MusicPostFeedResponse> page = response.body().getData();

                            List<MusicPostFeedResponse> list;
                            if (page != null && page.getContent() != null) {
                                list = page.getContent();
                            } else {
                                list = Collections.emptyList();
                            }

                            result.setValue(Resource.success(list));
                        } else {
                            result.setValue(Resource.error("오늘의 음악 조회 실패", null));
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<PageResponse<MusicPostFeedResponse>>> call,
                            Throwable t
                    ) {
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
//                        Log.d("PostRepositoryImpl",
//                                "onResponse called, httpCode=" + response.code());

                        if (response.isSuccessful() && response.body() != null) {
                            PageResponse<MusicPostFeedResponse> page = response.body().getData();

                            List<MusicPostFeedResponse> list;
                            if (page != null && page.getContent() != null) {
                                list = page.getContent();
                            } else {
                                list = Collections.emptyList();
                            }

//                            Log.d("PostRepositoryImpl",
//                                    "SUCCESS, list size = " + list.size());
                            result.setValue(Resource.success(list));
                        } else {
//                            Log.e("PostRepositoryImpl",
//                                    "getTodayPostsForGroup 실패, code=" + response.code());
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



    // 프로필의 오늘 공유한 음악 조회 메서드
    @Override
    public LiveData<Resource<TodayPostResponse>> getTodayPostByMember(Long memberId) {
        Log.d("PostRepositoryImpl", "getTodayPostByMember called with memberId: " + memberId);
        MutableLiveData<Resource<TodayPostResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        postApi.getTodayPostByMember(memberId)
                .enqueue(new Callback<ApiResponse<TodayPostResponse>>() {
                    @Override
                    public void onResponse(
                            Call<ApiResponse<TodayPostResponse>> call,
                            Response<ApiResponse<TodayPostResponse>> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<TodayPostResponse> body = response.body();
                            if (body.getData() != null) {
                                Log.d("PostRepositoryImpl", "getTodayPostByMember success");
                                result.setValue(Resource.success(body.getData()));
                            } else {
                                // 오늘 공유한 음악이 없는 경우
                                Log.d("PostRepositoryImpl", "getTodayPostByMember: no data (오늘 공유한 음악 없음)");
                                result.setValue(Resource.success(null));
                            }
                        } else if (response.code() == 404) {
                            // 404는 오늘 공유한 음악이 없다는 의미
                            Log.d("PostRepositoryImpl", "getTodayPostByMember: 404 (오늘 공유한 음악 없음)");
                            result.setValue(Resource.success(null));
                        } else {
                            Log.e("PostRepositoryImpl", "getTodayPostByMember 실패, code=" + response.code());
                            result.setValue(Resource.error("오늘 공유한 음악을 불러오지 못했어요.", null));
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<TodayPostResponse>> call,
                            Throwable t
                    ) {
                        Log.e("PostRepositoryImpl", "getTodayPostByMember onFailure: " + t.getMessage(), t);
                        result.setValue(Resource.error("네트워크 오류: " + t.getMessage(), null));
                    }
                });

        return result;
    }

}