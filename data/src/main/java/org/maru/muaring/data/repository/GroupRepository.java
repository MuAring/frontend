package org.maru.muaring.data.repository;

import org.maru.muaring.data.api.dto.GroupInviteResponse;

import androidx.lifecycle.LiveData;

import org.maru.muaring.core.util.Resource;

public interface GroupRepository {

    LiveData<Resource<GroupInviteResponse>> createInviteLink(Long groupId);
}