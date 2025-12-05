package org.maru.muaring.data.api.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GroupListResponse {

    @SerializedName("totalCount")
    private Long totalCount;

    @SerializedName("groups")
    private List<GroupSummary> groups;

    public Long getTotalCount() {
        return totalCount;
    }

    public List<GroupSummary> getGroups() {
        return groups;
    }
}
