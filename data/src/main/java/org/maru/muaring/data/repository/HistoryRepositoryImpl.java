package org.maru.muaring.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.HistoryApi;
import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.PageResponse;
import org.maru.muaring.data.api.dto.MusicHistoryResponse;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryRepositoryImpl implements HistoryRepository {

    private final HistoryApi api;

    @Inject
    public HistoryRepositoryImpl(HistoryApi api) {
        this.api = api;
    }

    // === Member History ===
    public LiveData<Resource<List<MusicHistoryResponse>>> getMemberHistory(
            Integer year, Integer month, int page
    ) {
        MutableLiveData<Resource<List<MusicHistoryResponse>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        api.getMemberHistory(year, month, page)
                .enqueue(new Callback<ApiResponse<PageResponse<MusicHistoryResponse>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<PageResponse<MusicHistoryResponse>>> call,
                                           Response<ApiResponse<PageResponse<MusicHistoryResponse>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<MusicHistoryResponse> list =
                                    response.body().getData().getContent();
                            result.setValue(Resource.success(list));
                        } else {
                            result.setValue(Resource.error("조회 실패", null));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<PageResponse<MusicHistoryResponse>>> call,
                                          Throwable t) {
                        result.setValue(Resource.error("네트워크 오류", null));
                    }
                });

        return result;
    }

    // === Group History ===
    public LiveData<Resource<List<MusicHistoryResponse>>> getGroupHistory(
            Long groupId, Integer year, Integer month, int page
    ) {
        MutableLiveData<Resource<List<MusicHistoryResponse>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        api.getGroupHistory(groupId, year, month, page)
                .enqueue(new Callback<ApiResponse<PageResponse<MusicHistoryResponse>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<PageResponse<MusicHistoryResponse>>> call,
                                           Response<ApiResponse<PageResponse<MusicHistoryResponse>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<MusicHistoryResponse> list =
                                    response.body().getData().getContent();
                            result.setValue(Resource.success(list));
                        } else {
                            result.setValue(Resource.error("조회 실패", null));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<PageResponse<MusicHistoryResponse>>> call,
                                          Throwable t) {
                        result.setValue(Resource.error("네트워크 오류", null));
                    }
                });

        return result;
    }
}
