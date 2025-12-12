package org.maru.muaring.data.repository;

import org.maru.muaring.core.common.Callback;
import org.maru.muaring.data.api.dto.MemberSearchItemDto;

import java.util.List;

public interface FollowRepository {

    void followMember(long memberId, Callback<Void> callback);

    void unfollowMember(long memberId, Callback<Void> callback);

    interface SearchMembersCallback {
        void onSuccess(List<MemberSearchItemDto> members);
        void onError(Throwable t);
    }

}
