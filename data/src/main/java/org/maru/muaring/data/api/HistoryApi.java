package org.maru.muaring.data.api;

import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.MusicHistoryResponse;
import org.maru.muaring.data.api.dto.PageResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface HistoryApi {

    // === Member History ===
    @GET("history")
    Call<ApiResponse<PageResponse<MusicHistoryResponse>>> getMemberHistory(
            @Query("year") Integer year,
            @Query("month") Integer month,
            @Query("page") Integer page
    );

    // === Group History ===
    @GET("groups/{groupId}/history")
    Call<ApiResponse<PageResponse<MusicHistoryResponse>>> getGroupHistory(
            @Path("groupId") Long groupId,
            @Query("year") Integer year,
            @Query("month") Integer month,
            @Query("page") Integer page
    );
}
