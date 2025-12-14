package org.maru.muaring.data.api;

import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.LibraryMusicDTO;
import org.maru.muaring.data.api.dto.SpotifyExportRequest;
import org.maru.muaring.data.api.dto.LibraryMusicListResponseDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LibraryApi {

    @GET("/library")
    Call<ApiResponse<LibraryMusicListResponseDto>> getLibrary();

    @HTTP(method = "DELETE", path = "/library/delete", hasBody = true)
    Call<ApiResponse<Void>> deleteMusic(@Body List<Long> ids);

    @DELETE("/library/delete/{musicId}")
    Call<ApiResponse<Void>> deleteOneMusic(@Path("musicId") Long musicId);
    
    @POST("/library/add/{musicId}")
    Call<ApiResponse<LibraryMusicDTO>> addMusicToLibrary(
            @Path("musicId") Long musicId,
            @Query("category") String category
    );

    @POST("/library/export")
    Call<Void> exportToSpotify(@Body SpotifyExportRequest request);

}
