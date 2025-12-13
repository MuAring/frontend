package org.maru.muaring.data.api;

import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.GroupCategoryResponse;
import org.maru.muaring.data.api.dto.GroupCreateRequest;
import org.maru.muaring.data.api.dto.GroupCreateResponse;
import org.maru.muaring.data.api.dto.GroupInviteResponse;
import org.maru.muaring.data.api.dto.GroupListResponse;
import org.maru.muaring.data.api.dto.GroupMemberResponse;
import org.maru.muaring.data.api.dto.InvitePreviewResponse;
import org.maru.muaring.data.api.dto.GroupProfileResponse;
import org.maru.muaring.data.api.dto.MyGroupListResponse;
import org.maru.muaring.data.api.dto.PageResponse;
import org.maru.muaring.data.api.dto.TodayMusicPostResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GroupApi {

    // 그룹 생성
    @POST("/groups")
    Call<ApiResponse<GroupCreateResponse>> createGroup(
            @Body GroupCreateRequest request
    );

    // 초대 링크 생성
    @POST("/invites/groups/{groupId}")
    Call<ApiResponse<GroupInviteResponse>> createInviteLink(
            @Path("groupId") Long groupId
    );

    // 초대 링크 미리보기
    @GET("/invites/preview/{inviteToken}")
    Call<ApiResponse<InvitePreviewResponse>> getInvitePreview(
            @Path("inviteToken") String inviteToken
    );

    // 초대 링크로 그룹 가입
    @POST("/invites/{inviteToken}/join")
    Call<ApiResponse<Void>> joinByInviteToken(
            @Path("inviteToken") String inviteToken
    );


    // 그룹 검색 결과 조회
    @GET("/groups")
    Call<ApiResponse<GroupListResponse>> searchGroups(
            @Query("name") String name,
            @Query("isPublic") Boolean isPublic,
            @Query("categoryIds") List<Long> categoryIds,
            @Query("page") Integer page,
            @Query("size") Integer size
    );

    // 그룹 카테고리 조회
    @GET("/group-categories")
    Call<ApiResponse<List<GroupCategoryResponse>>> getGroupCategories();

    // 내 그룹 조회 (검색 기능 포함)
    // name이 null이면 전체 조회, 값이 있으면 검색
    @GET("/me/groups")
    Call<ApiResponse<MyGroupListResponse>> getMyGroups(
            @Query("name") String name
    );

    // 그룹 프로필 조회
    @GET("/groups/{groupId}")
    Call<ApiResponse<GroupProfileResponse>> getGroupProfile(
            @Path("groupId") Long groupId
    );

    // 공개 그룹 가입
    @POST("/groups/{groupId}/members")
    Call<ApiResponse<Void>> joinPublicGroup(
            @Path("groupId") Long groupId
    );

    // 그룹 멤버 목록 조회
    @GET("/groups/{groupId}/members")
    Call<ApiResponse<List<GroupMemberResponse>>> getGroupMembers(
            @Path("groupId") Long groupId,
            @Query("search") String search
    );

    // 그룹 오늘 공유한 음악 조회
    @GET("/groups/{groupId}/posts/today")
    Call<ApiResponse<PageResponse<TodayMusicPostResponse>>> getTodayGroupFeed(
            @Path("groupId") Long groupId,
            @Query("page") int page,
            @Query("size") int size
    );
}