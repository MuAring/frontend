package org.maru.muaring.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.StatsApi;
import org.maru.muaring.data.api.dto.DailyTopMusicResponse;
import org.maru.muaring.data.api.dto.ApiResponse;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class StatsRepositoryImpl implements StatsRepository {

    private final StatsApi statsApi;

    @Inject
    public StatsRepositoryImpl(StatsApi statsApi) {
        this.statsApi = statsApi;
    }

    @Override
    public LiveData<Resource<DailyTopMusicResponse>> getYesterdayTop3() {
        MutableLiveData<Resource<DailyTopMusicResponse>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        statsApi.getYesterdayTop3().enqueue(new Callback<ApiResponse<DailyTopMusicResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<DailyTopMusicResponse>> call,
                                   Response<ApiResponse<DailyTopMusicResponse>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    liveData.setValue(Resource.error("서버 응답 실패 (" + response.code() + ")", null));
                    return;
                }
                DailyTopMusicResponse data = response.body().getData();
                if (data == null) {
                    liveData.setValue(Resource.error("응답 데이터가 비어있습니다.", null));
                    return;
                }
                liveData.setValue(Resource.success(data));
            }

            @Override
            public void onFailure(Call<ApiResponse<DailyTopMusicResponse>> call, Throwable t) {
                liveData.setValue(Resource.error(
                        t != null && t.getMessage() != null ? t.getMessage() : "네트워크 오류",
                        null
                ));
            }
        });

        return liveData;
    }

    @Override
    public LiveData<Resource<DailyTopMusicResponse>> getLast7DaysTop3() {
        MutableLiveData<Resource<DailyTopMusicResponse>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        statsApi.getLast7DaysTop3().enqueue(new Callback<ApiResponse<DailyTopMusicResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<DailyTopMusicResponse>> call,
                                   Response<ApiResponse<DailyTopMusicResponse>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    liveData.setValue(Resource.error("서버 응답 실패 (" + response.code() + ")", null));
                    return;
                }
                DailyTopMusicResponse data = response.body().getData();
                if (data == null) {
                    liveData.setValue(Resource.error("응답 데이터가 비어있습니다.", null));
                    return;
                }
                liveData.setValue(Resource.success(data));
            }

            @Override
            public void onFailure(Call<ApiResponse<DailyTopMusicResponse>> call, Throwable t) {
                liveData.setValue(Resource.error(
                        t != null && t.getMessage() != null ? t.getMessage() : "네트워크 오류",
                        null
                ));
            }
        });

        return liveData;
    }
}
