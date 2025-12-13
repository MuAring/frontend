package org.maru.muaring.data.api;

import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.FollowListResponse;
import org.maru.muaring.data.api.dto.FollowResponseDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FollowApi {

    @POST("/follow/request")
    Call<ApiResponse<FollowResponseDTO>> followMember(
            @Query("followeeId") Long memberId
    );

    @DELETE("/follow/unfollow")
    Call<ApiResponse<Void>> unfollowMember(
            @Query("followeeId") Long memberId
    );

    @GET("/follow/{memberId}/followers")
    Call<ApiResponse<List<FollowListResponse>>> getFollowers(
            @Path("memberId") long memberId
    );

    @GET("/follow/{memberId}/followings")
    Call<ApiResponse<List<FollowListResponse>>> getFollowings(
            @Path("memberId") long memberId
    );


}
