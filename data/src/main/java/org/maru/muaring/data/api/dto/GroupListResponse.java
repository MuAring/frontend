package org.maru.muaring.data.api.dto;

import java.util.List;

public class GroupListResponse {

    private Long totalCount;
    private List<GroupSummary> groups;

    public Long getTotalCount() {
        return totalCount;
    }

    public List<GroupSummary> getGroups() {
        return groups;
    }
}
