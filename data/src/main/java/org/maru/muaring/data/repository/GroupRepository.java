package org.maru.muaring.data.repository;

import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.GroupCategoryResponse;
import org.maru.muaring.data.api.dto.GroupCreateRequest;
import org.maru.muaring.data.api.dto.GroupCreateResponse;
import org.maru.muaring.data.api.dto.GroupInviteResponse;

import androidx.lifecycle.LiveData;

import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.dto.GroupSummary;

import java.util.List;

import retrofit2.Call;

public interface GroupRepository {

    Call<ApiResponse<GroupCreateResponse>> createGroup(GroupCreateRequest request);

    LiveData<Resource<GroupInviteResponse>> createInviteLink(Long groupId);

    void searchGroups(
            String name,
            int page,
            int size,
            SearchGroupsCallback callback
    );

    interface SearchGroupsCallback {
        void onSuccess(List<GroupSummary> groups);
        void onError(Throwable t);
    }

    Call<ApiResponse<List<GroupCategoryResponse>>> getGroupCategories();
}