package org.maru.muaring.data.api;

import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.GroupInviteResponse;
import org.maru.muaring.data.api.dto.GroupListResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GroupApi {

    // 초대 링크 생성
    @POST("groups/{groupId}/invites")
    Call<ApiResponse<GroupInviteResponse>> createInviteLink(
            @Path("groupId") Long groupId
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

}