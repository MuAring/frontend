package org.maru.muaring.feature.member.ui;

import android.content.Context;
import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import org.maru.muaring.core.common.Callback;
import org.maru.muaring.data.api.dto.ImageCreateRequest;
import org.maru.muaring.data.api.dto.MemberProfileCreateRequest;
import org.maru.muaring.data.api.dto.MemberProfileCreateResponse;
import org.maru.muaring.data.api.dto.NicknameCheckResponse;
import org.maru.muaring.data.helper.ImageUploadHelper;
import org.maru.muaring.data.helper.ProfileSetupState;
import org.maru.muaring.data.repository.ImageRepository;
import org.maru.muaring.data.repository.MemberRepository;
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
        ImageUploadHelper.uploadImage(
                imageUri,
                context,
                imageRepository,
                new Callback<ProfileSetupState.ImageUploaded>() {
                    @Override
                    public void onSuccess(ProfileSetupState.ImageUploaded uploaded) {
                        state.postValue(uploaded);

                        // 이미지 정보 저장 (기존 유지)
                        setUploadedImageInfo(
                                uploaded.s3Key,
                                uploaded.fileName,
                                uploaded.fileType,
                                uploaded.fileSize
                        );
                    }

                    @Override
                    public void onError(Exception e) {
                        state.postValue(new ProfileSetupState.Error("이미지 업로드 실패"));
                    }
                }
        );
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