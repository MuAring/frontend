package org.maru.muaring.data.api;

import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.SpotifyTrackResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UploadApi {
    @GET("/music/search")
    Call<ApiResponse<List<SpotifyTrackResponse>>> searchMusic(
            @Query("query") String query
    );
}
