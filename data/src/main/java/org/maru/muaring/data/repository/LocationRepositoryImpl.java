package org.maru.muaring.data.repository;

import org.maru.muaring.core.common.Callback;
import org.maru.muaring.data.api.LocationApi;
import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.LocationRequestDTO;

import retrofit2.Call;
import retrofit2.Response;

public class LocationRepositoryImpl implements LocationRepository {
    private final LocationApi locationApi;

    public LocationRepositoryImpl(LocationApi locationApi) {
        this.locationApi = locationApi;
    }

    @Override
    public void updateLocation(LocationRequestDTO request, Callback<Void> callback) {

        locationApi.updateLocation(request).enqueue(new retrofit2.Callback<>() {

            @Override
            public void onResponse(
                    Call<ApiResponse<Void>> call,
                    Response<ApiResponse<Void>> response
            ) {
                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<Void> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(null);
                    } else {
                        callback.onError(new Exception("API 요청 실패"));
                    }

                } else {
                    callback.onError(new Exception("서버 응답 오류: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onError(new Exception("네트워크 오류: " + t.getMessage()));
            }
        });
    }
}
