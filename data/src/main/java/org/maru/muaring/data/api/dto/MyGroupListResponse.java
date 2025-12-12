package org.maru.muaring.data.api.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MyGroupListResponse {

    @SerializedName("totalCount")
    private Long totalCount;

    @SerializedName("groups")
    private List<MyGroupSummary> groups;

    public Long getTotalCount() {
        return totalCount;
    }

    public List<MyGroupSummary> getGroups() {
        return groups;
    }

}