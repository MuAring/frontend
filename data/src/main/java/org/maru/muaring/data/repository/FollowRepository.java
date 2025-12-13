package org.maru.muaring.data.repository;

import org.maru.muaring.core.common.Callback;
import org.maru.muaring.data.api.FollowApi;
import org.maru.muaring.data.api.dto.FollowListResponse;
import org.maru.muaring.data.api.dto.MemberSearchItemDto;

import java.util.List;

import javax.inject.Inject;

public interface FollowRepository {

    void followMember(long memberId, Callback<Void> callback);

    void unfollowMember(long memberId, Callback<Void> callback);

    void getFollowers(long memberId, Callback<List<FollowListResponse>> callback);

    void getFollowings(long memberId, Callback<List<FollowListResponse>> callback);
    interface SearchMembersCallback {
        void onSuccess(List<MemberSearchItemDto> members);
        void onError(Throwable t);
    }

}
