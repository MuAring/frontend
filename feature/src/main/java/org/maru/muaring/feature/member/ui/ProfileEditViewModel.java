package org.maru.muaring.feature.member.ui;

import android.content.Context;
import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import org.maru.muaring.core.common.Callback;
import org.maru.muaring.data.api.dto.ImageCreateRequest;
import org.maru.muaring.data.api.dto.MemberProfileSettingReadResponse;
import org.maru.muaring.data.api.dto.MemberProfileUpdateRequest;
import org.maru.muaring.data.api.dto.NicknameCheckResponse;
import org.maru.muaring.data.helper.ImageUploadHelper;
import org.maru.muaring.data.helper.ProfileSetupState;
import org.maru.muaring.data.repository.ImageRepository;
import org.maru.muaring.data.repository.MemberRepository;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProfileEditViewModel extends ViewModel {

    private final MutableLiveData<String> imageUrl = new MutableLiveData<>();
    private final MutableLiveData<String> nickname = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isAccountPublic = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isDiscoveryEnabled = new MutableLiveData<>();

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;

    private String uploadedS3Key;
    private String uploadedFileName;
    private String uploadedFileType;
    private long uploadedFileSize;

    private String originalNickname;
    private Boolean originalIsAccountPublic;
    private Boolean originalIsDiscoveryEnabled;
    private String originalImageUrl;


    @Inject
    public ProfileEditViewModel(MemberRepository memberRepository, ImageRepository imageRepository) {
        this.memberRepository = memberRepository;
        this.imageRepository = imageRepository;
    }

    private final MutableLiveData<ProfileSetupState> state = new MutableLiveData<>(new ProfileSetupState.Idle());
    public LiveData<ProfileSetupState> getState() { return state; }

    private String s3Key;
    private String fileName;
    private String fileType;
    private Long fileSize;

    public LiveData<String> getImageUrl() { return imageUrl; }
    public LiveData<String> getNickname() { return nickname; }
    public LiveData<Boolean> getIsAccountPublic() { return isAccountPublic; }
    public LiveData<Boolean> getIsDiscoveryEnabled() { return isDiscoveryEnabled; }

    public void setImageUrl(String url) { imageUrl.setValue(url); }
    public void setNickname(String name) { nickname.setValue(name); }
    public void setIsAccountPublic(Boolean value) { isAccountPublic.setValue(value); }
    public void setIsDiscoveryEnabled(Boolean value) { isDiscoveryEnabled.setValue(value);}

    // 설정된 프로필 정보 조회
    public void loadProfile() {
        memberRepository.loadProfile(new Callback<>() {
            @Override
            public void onSuccess(MemberProfileSettingReadResponse response) {

                // UI
                imageUrl.postValue(response.getImageUrl());
                nickname.postValue(response.getNickname());
                isAccountPublic.postValue(response.getIsAccountPublic());
                isDiscoveryEnabled.postValue(response.getIsDiscoveryEnabled());

                // 원래값
                originalImageUrl = response.getImageUrl();
                originalNickname = response.getNickname();
                originalIsAccountPublic = response.getIsAccountPublic();
                originalIsDiscoveryEnabled = response.getIsDiscoveryEnabled();
            }

            @Override
            public void onError(Exception e) {
                errorMessage.postValue("프로필 정보를 불러오지 못했어요 🥲");
            }
        });
    }

    public void checkNickname(String nickname) {
        memberRepository.checkNickname(nickname, new Callback<>() {
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
                new Callback<>() {
                    @Override
                    public void onSuccess(ProfileSetupState.ImageUploaded uploaded) {
                        state.postValue(uploaded);
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

    public void setUploadedImageInfo(String s3Key, String fileName, String fileType, Long fileSize) {
        this.s3Key = s3Key;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }

    public void editProfile() {
        state.postValue(new ProfileSetupState.Loading());

        String nicknameValue = nickname.getValue();
        Boolean isAccountPublicValue = isAccountPublic.getValue();
        Boolean isDiscoveryEnabledValue = isDiscoveryEnabled.getValue();

        // 닉네임 변경 여부 검사
        String nicknameToSend = null;
        if (nicknameValue != null && !nicknameValue.equals(originalNickname)) {
            nicknameToSend = nicknameValue;
        }

        // 공개 여부 변경 여부
        Boolean isPublicToSend = null;
        if (isAccountPublicValue != null && !isAccountPublicValue.equals(originalIsAccountPublic)) {
            isPublicToSend = isAccountPublicValue;
        }

        // 발견 활성화 변경 여부
        Boolean isDiscoveryToSend = null;
        if (isDiscoveryEnabledValue != null && !isDiscoveryEnabledValue.equals(originalIsDiscoveryEnabled)) {
            isDiscoveryToSend = isDiscoveryEnabledValue;
        }

        // 이미지 변경 여부
        ImageCreateRequest imageRequest = null;
        if (uploadedS3Key != null) {
            imageRequest = ImageCreateRequest.create(
                    uploadedFileName,
                    uploadedFileType,
                    "MEMBER",
                    uploadedFileSize,
                    uploadedS3Key
            );
        }

        MemberProfileUpdateRequest request =
                MemberProfileUpdateRequest.create(
                        nicknameToSend,
                        imageRequest,
                        isPublicToSend,
                        isDiscoveryToSend
                );

        memberRepository.updateProfile(request, new Callback<>() {
            @Override
            public void onSuccess(Void unused) {
                state.postValue(new ProfileSetupState.ProfileCreated(nicknameValue));
            }

            @Override
            public void onError(Exception e) {
                state.postValue(new ProfileSetupState.Error("프로필 수정에 실패했습니다."));
            }
        });
    }
}