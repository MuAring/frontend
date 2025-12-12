package org.maru.muaring.data.api;

import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.GroupImageUploadRequest;
import org.maru.muaring.data.api.dto.ImageUploadRequest;
import org.maru.muaring.data.api.dto.PresignedUrlResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ImageApi {

    @POST("/images/upload-presigned-url")
    Call<ApiResponse<PresignedUrlResponse>> getUploadPresignedUrl(@Body ImageUploadRequest request);

//    @POST("/{imageId}/download-presigned-url")
//    Call<ApiResponse<PresignedUrlResponse>> getDownloadPresignedUrl(@Path("imageId") Long imageId);

    // 그룹 프로필 이미지 설정
    @POST("/images/group-upload")
    Call<ApiResponse<Void>> confirmGroupImageUpload(@Body GroupImageUploadRequest request);
}
