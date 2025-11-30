package org.maru.muaring.data.api.dto;

import java.util.List;

public class MyGroupListResponse {
    private Long totalCount;
    private List<MyGroupSummary> groups;

    public Long getTotalCount() { return totalCount; }
    public List<MyGroupSummary> getGroups() { return groups; }
}