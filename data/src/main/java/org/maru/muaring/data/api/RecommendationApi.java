package org.maru.muaring.data.api;

import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.GroupRecommendListResponseDto;
import org.maru.muaring.data.api.dto.MemberRecommendItemDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Path;

public interface RecommendationApi {

    // ===== 그룹 추천 =====
    @GET("/recommendations/groups-members")
    Call<ApiResponse<GroupRecommendListResponseDto>> getRecommendedGroups(
            @Query("limit") int limit
    );

    @POST("/recommendations/groups-members/{groupId}/click")
    Call<ApiResponse<Void>> logGroupClick(
            @Path("groupId") long groupId
    );

    @POST("/recommendations/groups-members/{groupId}/join")
    Call<ApiResponse<Void>> logGroupJoin(
            @Path("groupId") long groupId
    );

    // ===== 멤버 추천 =====
    @GET("/recommendations/members")
    Call<ApiResponse<List<MemberRecommendItemDto>>> getRecommendedMembers(
            @Query("limit") int limit
    );

    @POST("/recommendations/members/{targetMemberId}/click")
    Call<ApiResponse<Void>> logMemberClick(
            @Path("targetMemberId") long targetMemberId
    );

    @POST("/recommendations/members/{targetMemberId}/follow")
    Call<ApiResponse<Void>> logMemberFollow(
            @Path("targetMemberId") long targetMemberId
    );
}
