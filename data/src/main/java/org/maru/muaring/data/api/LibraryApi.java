package org.maru.muaring.data.api;

import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.ExportRequest;
import org.maru.muaring.data.api.dto.LibraryMusicListResponseDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface LibraryApi {
    @POST("/library/export")
    Call<Void> exportToSpotify(@Body ExportRequest request);

    @GET("/library")
    Call<ApiResponse<LibraryMusicListResponseDto>> getLibrary();

}
