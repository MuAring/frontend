package org.maru.muaring.data.repository;

import androidx.lifecycle.LiveData;

import org.maru.muaring.core.common.Callback;
import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.dto.MemberProfileCreateRequest;
import org.maru.muaring.data.api.dto.MemberProfileCreateResponse;
import org.maru.muaring.data.api.dto.MemberSettingsResponse;
import org.maru.muaring.data.api.dto.NicknameCheckResponse;

public interface MemberRepository {

    void checkNickname(String nickname, Callback<NicknameCheckResponse> callback);
    void createProfile(MemberProfileCreateRequest request, Callback<MemberProfileCreateResponse> callback);

    LiveData<Resource<MemberSettingsResponse>> getMySettings();
}