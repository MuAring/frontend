package org.maru.muaring.data.api;

import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.CommentCreateRequest;
import org.maru.muaring.data.api.dto.CommentReadResponse;
import org.maru.muaring.data.api.dto.CommentResponse;
import org.maru.muaring.data.api.dto.MusicPostFeedResponse;
import org.maru.muaring.data.api.dto.PageResponse;
import org.maru.muaring.data.api.dto.PostDetailReadResponse;
import org.maru.muaring.data.api.dto.TodayPostResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface PostApi {

    // "나" 기준 오늘의 음악
    @GET("/post/followee/today")
    Call<ApiResponse<PageResponse<MusicPostFeedResponse>>> getTodayPostsForMe();

    // 내 공유한 음악 (내 게시물만)
    @GET("/post/me")
    Call<ApiResponse<PageResponse<MusicPostFeedResponse>>> getPostsForMeOnly();

    // 그룹 기준 오늘의 음악
    @GET("/groups/{groupId}/posts/today")
    Call<ApiResponse<PageResponse<MusicPostFeedResponse>>> getTodayPostsForGroup(
            @Path("groupId") Long groupId
    );

    // 그룹 공유한 음악
    @GET("/groups/{groupId}/posts")
    Call<ApiResponse<PageResponse<MusicPostFeedResponse>>> getGroupPosts(
            @Path("groupId") Long groupId
    );

    // 프로필의 오늘 공유한 음악 조회
    @GET("/post/{memberId}/today")
    Call<ApiResponse<TodayPostResponse>> getTodayPostByMember(
            @Path("memberId") Long memberId
    );

    // 게시물 상세 조회
    @GET("/posts/{postId}")
    Call<ApiResponse<PostDetailReadResponse>> getPostDetail(
            @Path("postId") Long postId
    );

    @GET("/posts/{postId}/comments")
    Call<ApiResponse<List<CommentReadResponse>>> getComments(
            @Path("postId") Long postId
    );

    @POST("/posts/{postId}/comments")
    Call<ApiResponse<CommentResponse>> addComment(
            @Path("postId") Long postId,
            @Body CommentCreateRequest request
    );

    @POST("/comments/{commentId}/replies")
    Call<ApiResponse<CommentResponse>> addReply(
            @Path("commentId") Long commentId,
            @Body CommentCreateRequest request
    );
}
