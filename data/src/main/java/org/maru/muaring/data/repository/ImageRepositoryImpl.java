package org.maru.muaring.data.repository;

import org.maru.muaring.core.common.Callback;
import org.maru.muaring.data.api.ImageApi;
import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.ImageUploadRequest;
import org.maru.muaring.data.api.dto.PresignedUrlResponse;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class ImageRepositoryImpl implements ImageRepository{

    private final ImageApi imageApi;

    public ImageRepositoryImpl(ImageApi imageApi) {
        this.imageApi = imageApi;
    }

    @Override
    public void getUploadPresignedUrl(ImageUploadRequest request, Callback<PresignedUrlResponse> callback) {
        imageApi.getUploadPresignedUrl(request).enqueue(new retrofit2.Callback<>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<PresignedUrlResponse>> call,
                    Response<ApiResponse<PresignedUrlResponse>> response
            ) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(new Exception("API 응답을 가져오던 중 문제가 발생했습니다."));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PresignedUrlResponse>> call, Throwable t) {
                callback.onError(new Exception(t.getMessage() + " 네트워크 오류가 발생했습니다."));
            }
        });
    }

    @Override
    public void uploadToS3(String presignedUrl, String fileName, byte[] bytes, String fileType, Callback<Void> callback) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(presignedUrl)
                .put(RequestBody.create(MediaType.parse(fileType), bytes))
                .addHeader("Content-Type", fileType)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) callback.onSuccess(null);
                else callback.onError(new Exception("S3 업로드가 실패했습니다. : " + response.message()));
            }

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                callback.onError(e);
            }
        });
    }
}
