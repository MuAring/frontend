package org.maru.muaring.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.LibraryApi;
import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.LibraryMusicDTO;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class LibraryRepositoryImpl implements LibraryRepository {

    private static final String TAG = "LibraryRepository";
    private final LibraryApi libraryApi;

    public LibraryRepositoryImpl(LibraryApi libraryApi) {
        this.libraryApi = libraryApi;
    }

    @Override
    public LiveData<Resource<LibraryMusicDTO>> addMusicToLibrary(Long musicId, String category) {
        MutableLiveData<Resource<LibraryMusicDTO>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Log.d(TAG, "addMusicToLibrary 요청 musicId=" + musicId + ", category=" + category);

        libraryApi.addMusicToLibrary(musicId, category)
                .enqueue(new Callback<ApiResponse<LibraryMusicDTO>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<LibraryMusicDTO>> call,
                                           Response<ApiResponse<LibraryMusicDTO>> response) {

                        int code = response.code();
                        Log.d(TAG, "addMusicToLibrary onResponse code=" + code);

                        if (response.isSuccessful() && response.body() != null) {
                            result.setValue(Resource.success(response.body().getData()));
                            return;
                        }

                        // 서버가 "이미 존재"를 302로 주는 케이스: UI는 true로 맞춰야 함
                        if (code == 302) {
                            Log.w(TAG, "already in library -> treat as success");
                            result.setValue(Resource.success(null)); // data 없어도 SUCCESS면 VM이 applyLibraryResult(true) 탐
                            return;
                        }

                        result.setValue(Resource.error("보관함 추가 실패 (HTTP " + code + ")", null));
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<LibraryMusicDTO>> call, Throwable t) {
                        Log.e(TAG, "addMusicToLibrary onFailure", t);
                        result.setValue(Resource.error("네트워크 오류: " + t.getMessage(), null));
                    }
                });

        return result;
    }

    @Override
    public LiveData<Resource<Void>> deleteMusicFromLibrary(List<Long> musicIds) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));


        libraryApi.deleteMusic(musicIds)
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call,
                                           Response<ApiResponse<Void>> response) {
                        Log.d(TAG, "deleteMusicFromLibrary onResponse code=" + response.code());

                        if (response.isSuccessful()) {
                            result.setValue(Resource.success(null)); // 성공이면 null
                        } else {
                            result.setValue(Resource.error(
                                    "보관함 삭제 실패 (HTTP " + response.code() + ")", null
                            ));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                        Log.e(TAG, "deleteMusicFromLibrary onFailure", t);
                        result.setValue(Resource.error("네트워크 오류: " + t.getMessage(), null));
                    }
                });

        return result;
    }

    @Override
    public LiveData<Resource<Void>> deleteOneMusicFromLibrary(Long musicId) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Log.d(TAG, "deleteOneMusicFromLibrary 요청 musicId=" + musicId);

        libraryApi.deleteOneMusic(musicId)
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call,
                                           Response<ApiResponse<Void>> response) {
                        int code = response.code();
                        Log.d(TAG, "deleteOneMusicFromLibrary onResponse code=" + code);

                        if (response.isSuccessful()) {
                            result.setValue(Resource.success(null));
                        } else {
                            result.setValue(Resource.error(
                                    "보관함에서 하나 삭제 실패 (HTTP " + code + ")", null
                            ));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                        Log.e(TAG, "deleteOneMusicFromLibrary onFailure", t);
                        result.setValue(Resource.error("네트워크 오류: " + t.getMessage(), null));
                    }
                });

        return result;
    }

}