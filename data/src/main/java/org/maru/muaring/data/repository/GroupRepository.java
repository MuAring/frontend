package org.maru.muaring.data.repository;

import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.GroupCategoryResponse;
import org.maru.muaring.data.api.dto.GroupCreateRequest;
import org.maru.muaring.data.api.dto.GroupCreateResponse;
import org.maru.muaring.data.api.dto.GroupInviteResponse;

import androidx.lifecycle.LiveData;

import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.dto.GroupProfileResponse;
import org.maru.muaring.data.api.dto.GroupSummary;
import org.maru.muaring.data.api.dto.InvitePreviewResponse;
import org.maru.muaring.data.api.dto.MyGroupSummary;

import java.util.List;

import retrofit2.Call;

public interface GroupRepository {

    Call<ApiResponse<GroupCreateResponse>> createGroup(GroupCreateRequest request);

    LiveData<Resource<GroupInviteResponse>> createInviteLink(Long groupId);

    // 초대 링크 미리보기
    LiveData<Resource<InvitePreviewResponse>> getInvitePreview(String inviteToken);

    // 초대 링크로 그룹 가입
    LiveData<Resource<Void>> joinByInviteToken(String inviteToken);

    void searchGroups(
            String name,
            int page,
            int size,
            SearchGroupsCallback callback
    );

    // 홈 화면용 내 그룹 조회
    LiveData<Resource<List<MyGroupSummary>>> getMyGroups();

    interface SearchGroupsCallback {
        void onSuccess(List<GroupSummary> groups);
        void onError(Throwable t);
    }

    Call<ApiResponse<List<GroupCategoryResponse>>> getGroupCategories();

    LiveData<Resource<GroupProfileResponse>> getGroupProfile(Long groupId);

    void joinPublicGroup(Long groupId, JoinGroupCallback callback);

    interface JoinGroupCallback {
        void onSuccess();
        void onError(Throwable t);
    }
}