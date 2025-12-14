package org.maru.muaring.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.NearbyApi;
import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.TodayNearbyMusicDTO;

import java.util.List;

import jakarta.inject.Inject;
import retrofit2.Call;
import retrofit2.Response;

public class NearbyRepositoryImpl implements NearbyRepository {

    private final NearbyApi nearbyApi;

    @Inject
    public NearbyRepositoryImpl(NearbyApi nearbyApi) {
        this.nearbyApi = nearbyApi;
    }

    @Override
    public LiveData<Resource<List<TodayNearbyMusicDTO>>> getTodayNearbyMusic(
            double lat,
            double lng,
            double radiusKm
    ) {
        MutableLiveData<Resource<List<TodayNearbyMusicDTO>>> liveData =
                new MutableLiveData<>();

        liveData.setValue(Resource.loading(null));

        nearbyApi.getTodayNearbyMusic(lat, lng, radiusKm)
                .enqueue(new retrofit2.Callback<ApiResponse<List<TodayNearbyMusicDTO>>>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse<List<TodayNearbyMusicDTO>>> call,
                            Response<ApiResponse<List<TodayNearbyMusicDTO>>> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().getData() != null) {

                            liveData.setValue(
                                    Resource.success(response.body().getData())
                            );

                        } else {
                            liveData.setValue(
                                    Resource.error("인근 사용자 음악 조회 실패", null)
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<List<TodayNearbyMusicDTO>>> call,
                            Throwable t) {

                        liveData.setValue(
                                Resource.error(t.getMessage(), null)
                        );
                    }
                });

        return liveData;
    }
}

