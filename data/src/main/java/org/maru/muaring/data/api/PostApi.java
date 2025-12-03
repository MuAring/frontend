package org.maru.muaring.data.api;

import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.MusicPostFeedResponse;
import org.maru.muaring.data.api.dto.PageResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface PostApi {

    // "나" 기준 오늘의 음악
    @GET("/post/followee/today")
    Call<ApiResponse<List<MusicPostFeedResponse>>> getTodayPostsForMe();

    // 그룹 기준 오늘의 음악
    @GET("/groups/{groupId}/posts/today")
    Call<ApiResponse<PageResponse<MusicPostFeedResponse>>> getTodayPostsForGroup(
            @Path("groupId") Long groupId
    );

}
