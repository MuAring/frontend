package org.maru.muaring.feature.group.ui;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import org.maru.muaring.core.common.Callback;
import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.dto.GroupImageUploadRequest;
import org.maru.muaring.data.api.dto.ImageUploadRequest;
import org.maru.muaring.data.api.dto.PresignedUrlResponse;
import org.maru.muaring.data.repository.ImageRepository;
import javax.inject.Inject;

@HiltViewModel
public class GroupProfileViewModel extends ViewModel {

    private static final String TAG = "GroupProfileViewModel";
    private static final int MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB

    private final ImageRepository imageRepository;

    private final MutableLiveData<Resource<String>> uploadStatus = new MutableLiveData<>();

    @Inject
    public GroupProfileViewModel(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public LiveData<Resource<String>> getUploadStatus() {
        return uploadStatus;
    }

    public void uploadGroupProfileImage(
            Long groupId,
            String fileName,
            String fileType,
            byte[] imageBytes,
            long fileSize
    ) {
        // 파일 크기 검증
        if (fileSize > MAX_IMAGE_SIZE) {
            uploadStatus.setValue(Resource.error("이미지 크기는 5MB를 초과할 수 없습니다.", null));
            return;
        }

        uploadStatus.setValue(Resource.loading(null));

        // 1단계: Presigned URL 발급
        ImageUploadRequest request = ImageUploadRequest.create(
                fileName,
                fileType,
                "GROUP",
                fileSize,
                groupId
        );

        imageRepository.getUploadPresignedUrl(request, new Callback<PresignedUrlResponse>() {
            @Override
            public void onSuccess(PresignedUrlResponse response) {
                // 2단계: S3 업로드
                uploadToS3(response, fileName, imageBytes, fileType, groupId, fileSize);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Presigned URL 발급 실패", e);
                uploadStatus.postValue(Resource.error("업로드 준비 중 오류가 발생했습니다.", null));
            }
        });
    }

    private void uploadToS3(
            PresignedUrlResponse response,
            String fileName,
            byte[] imageBytes,
            String fileType,
            Long groupId,
            long fileSize
    ) {
        imageRepository.uploadToS3(
                response.getPresignedUrl(),
                fileName,
                imageBytes,
                fileType,
                new Callback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        // 3단계: DB 저장 및 그룹 연결
                        confirmGroupImageUpload(groupId, response.getS3Key(), fileName, fileType, fileSize);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "S3 업로드 실패", e);
                        uploadStatus.postValue(Resource.error("이미지 업로드에 실패했습니다.", null));
                    }
                }
        );
    }

    private void confirmGroupImageUpload(
            Long groupId,
            String s3Key,
            String fileName,
            String fileType,
            long fileSize
    ) {
        GroupImageUploadRequest request = GroupImageUploadRequest.create(
                groupId,
                s3Key,
                fileName,
                fileType,
                fileSize
        );

        imageRepository.confirmGroupImageUpload(request, new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.d(TAG, "그룹 프로필 이미지 설정 완료");
                uploadStatus.postValue(Resource.success("그룹 프로필 이미지가 설정되었습니다!"));
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "그룹 이미지 설정 실패", e);
                uploadStatus.postValue(Resource.error("이미지 설정에 실패했습니다.", null));
            }
        });
    }
}