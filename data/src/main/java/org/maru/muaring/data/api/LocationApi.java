package org.maru.muaring.data.api;

import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.LocationRequestDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LocationApi {
    @POST("/location")
    Call<ApiResponse<Void>> updateLocation(@Body LocationRequestDTO request);
}
