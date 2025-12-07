package org.maru.muaring.data.repository;

import androidx.lifecycle.LiveData;

import org.maru.muaring.core.common.Callback;
import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.dto.MemberProfileCreateRequest;
import org.maru.muaring.data.api.dto.MemberProfileCreateResponse;
import org.maru.muaring.data.api.dto.MemberSearchItemDto;
import org.maru.muaring.data.api.dto.MemberSettingsResponse;
import org.maru.muaring.data.api.dto.MemberProfileSettingReadResponse;
import org.maru.muaring.data.api.dto.MemberProfileUpdateRequest;
import org.maru.muaring.data.api.dto.NicknameCheckResponse;

import java.util.List;

public interface MemberRepository {

    void checkNickname(String nickname, Callback<NicknameCheckResponse> callback);
    void createProfile(MemberProfileCreateRequest request, Callback<MemberProfileCreateResponse> callback);
    void loadProfile(Callback<MemberProfileSettingReadResponse> callback);
    void updateProfile(MemberProfileUpdateRequest request, Callback<Void> callback);

    LiveData<Resource<MemberSettingsResponse>> getMySettings();

    void searchMembers(String name, int page, int size, SearchMembersCallback callback);

    interface SearchMembersCallback {
        void onSuccess(List<MemberSearchItemDto> members);
        void onError(Throwable t);
    }
}