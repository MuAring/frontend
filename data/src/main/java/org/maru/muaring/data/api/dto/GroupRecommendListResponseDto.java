package org.maru.muaring.data.api.dto;

import java.util.List;

public class GroupRecommendListResponseDto {

    private Long memberId;
    private List<GroupRecommendItemDto> groups;

    public Long getMemberId() {
        return memberId;
    }

    public List<GroupRecommendItemDto> getGroups() {
        return groups;
    }
}
