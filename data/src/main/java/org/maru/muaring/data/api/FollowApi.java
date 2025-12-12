package org.maru.muaring.data.api;

import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.FollowResponseDTO;

import retrofit2.Call;
import retrofit2.http.DELETE;
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
}
