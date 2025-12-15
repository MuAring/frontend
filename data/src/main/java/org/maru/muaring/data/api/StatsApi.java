package org.maru.muaring.data.api;

import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.DailyTopMusicResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface StatsApi {

    @GET("/stats/musics/top3/yesterday")
    Call<ApiResponse<DailyTopMusicResponse>> getYesterdayTop3();

    @GET("/stats/musics/top3/last7days")
    Call<ApiResponse<DailyTopMusicResponse>> getLast7DaysTop3();
}
