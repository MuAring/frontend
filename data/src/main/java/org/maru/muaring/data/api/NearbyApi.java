package org.maru.muaring.data.api;

import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.TodayNearbyMusicDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NearbyApi {
    @GET("/nearby/today-music")
    Call<ApiResponse<List<TodayNearbyMusicDTO>>> getTodayNearbyMusic(
            @Query("lat") double lat,
            @Query("lng") double lng,
            @Query("radiusKm") double radiusKm
    );
}
