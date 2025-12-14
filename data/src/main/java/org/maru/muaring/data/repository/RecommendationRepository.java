package org.maru.muaring.data.repository;

import org.maru.muaring.core.common.Callback;
import org.maru.muaring.data.api.dto.GroupRecommendListResponseDto;
import org.maru.muaring.data.api.dto.MemberRecommendItemDto;

import java.util.List;

public interface RecommendationRepository {

    // 그룹 추천
    void getGroupRecommendations(
            int limit,
            Callback<GroupRecommendListResponseDto> callback
    );

    void logGroupClick(long groupId);

    void logGroupJoin(long groupId);

    // 멤버 추천
    void getMemberRecommendations(
            int limit,
            Callback<List<MemberRecommendItemDto>> callback
    );

    void logMemberClick(long targetMemberId);

    void logMemberFollow(long targetMemberId);
}