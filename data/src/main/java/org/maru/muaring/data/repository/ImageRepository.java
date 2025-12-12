package org.maru.muaring.data.repository;

import org.maru.muaring.core.common.Callback;
import org.maru.muaring.data.api.dto.GroupImageUploadRequest;
import org.maru.muaring.data.api.dto.ImageUploadRequest;
import org.maru.muaring.data.api.dto.PresignedUrlResponse;

public interface ImageRepository {

    void getUploadPresignedUrl(ImageUploadRequest request, Callback<PresignedUrlResponse> call);
    void uploadToS3(String presignedUrl, String fileName, byte[] bytes, String fileType, Callback<Void> callback);

    // 그룹 프로필 이미지 설정
    void confirmGroupImageUpload(GroupImageUploadRequest request, Callback<Void> callback);
}
