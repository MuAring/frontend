package org.maru.muaring.feature.member.ui;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import org.maru.muaring.core.common.Callback;
import org.maru.muaring.data.api.dto.ImageCreateRequest;
import org.maru.muaring.data.api.dto.ImageUploadRequest;
import org.maru.muaring.data.api.dto.MemberProfileCreateRequest;
import org.maru.muaring.data.api.dto.MemberProfileCreateResponse;
import org.maru.muaring.data.api.dto.NicknameCheckResponse;
import org.maru.muaring.data.api.dto.PresignedUrlResponse;
import org.maru.muaring.data.repository.ImageRepository;
import org.maru.muaring.data.repository.MemberRepository;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import dagger.hilt.android.lifecycle.HiltViewModel;
import javax.inject.Inject;

@HiltViewModel
public class ProfileSetupViewModel extends ViewModel {

    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;

    private final MutableLiveData<ProfileSetupState> state = new MutableLiveData<>(new ProfileSetupState.Idle());
    public LiveData<ProfileSetupState> getState() { return state; }

    private String s3Key;
    private String fileName;
    private String fileType;
    private Long fileSize;

    @Inject
    public ProfileSetupViewModel(MemberRepository memberRepository, ImageRepository imageRepository) {
        this.memberRepository = memberRepository;
        this.imageRepository = imageRepository;
    }

    public void checkNickname(String nickname) {
        memberRepository.checkNickname(nickname, new Callback<NicknameCheckResponse>() {
            @Override
            public void onSuccess(NicknameCheckResponse response) {
                if (!response.isDuplicated) state.postValue(new ProfileSetupState.NicknameAvailable(nickname));
                else state.postValue(new ProfileSetupState.NicknameUnAvailable(nickname));
            }

            @Override
            public void onError(Exception e) {
                state.postValue(new ProfileSetupState.Error("오류 발생"));
            }
        });
    }

    public void uploadProfileImage(Uri imageUri, Context context) {
        // 파일 읽기
        byte[] bytes = readBytes(imageUri, context);
        String fileName = getFileName(context, imageUri);
        String fileType = getMimeType(context, imageUri);
        ImageUploadRequest request = ImageUploadRequest.create(
                fileName,
                fileType,
                "MEMBER",
                (long) bytes.length,
                null
        );

        imageRepository.getUploadPresignedUrl(request, new Callback<>() {
            @Override
            public void onSuccess(PresignedUrlResponse response) {
                imageRepository.uploadToS3(response.presignedUrl, fileName, bytes, fileType, new Callback<>() {
                    @Override
                    public void onSuccess(Void unused) {
                        state.postValue(new ProfileSetupState.ImageUploaded(response.s3Key, fileName, fileType, (long) bytes.length));
                    }

                    @Override
                    public void onError(Exception e) {
                        state.postValue(new ProfileSetupState.Error("S3 업로드 실패"));
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                state.postValue(new ProfileSetupState.Error("오류 발생"));
            }
        });
    }

    public static byte[] readBytes(Uri uri, Context context) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;

            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }

            return byteBuffer.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getFileName(Context context, Uri uri) {
        String result = null;

        // content:// 형태일 경우
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }

        // null일 경우 URI 경로에서 추출 (file:// 형태)
        if (result == null) {
            result = uri.getLastPathSegment();
        }

        return result;
    }

    public static String getMimeType(Context context, Uri uri) {
        String type = context.getContentResolver().getType(uri);
        if (type != null) return type;

        // fallback (확장자로 추론)
        String path = uri.getPath();
        if (path == null) return "image/*";

        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
    }

    public void createProfile(String nickname) {

        ImageCreateRequest imageRequest = null;

        if (s3Key != null && fileName != null && fileType != null) {
            imageRequest = ImageCreateRequest.create(
                    fileName,
                    fileType,
                    "MEMBER",
                    fileSize,
                    s3Key
            );
        }

        MemberProfileCreateRequest memberRequest = MemberProfileCreateRequest.create(nickname, imageRequest);
        memberRepository.createProfile(memberRequest, new Callback<MemberProfileCreateResponse>() {
            @Override
            public void onSuccess(MemberProfileCreateResponse result) {
                state.postValue(new ProfileSetupState.ProfileCreated(nickname));
            }

            @Override
            public void onError(Exception e) {
                state.postValue(new ProfileSetupState.Error("프로필 생성 실패"));
            }
        });
    }

    public void setUploadedImageInfo(String s3Key, String fileName, String fileType, Long fileSize) {
        this.s3Key = s3Key;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }
}