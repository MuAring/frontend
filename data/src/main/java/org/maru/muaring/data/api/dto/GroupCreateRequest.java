package org.maru.muaring.data.api.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GroupCreateRequest {

    private List<Long> groupCategoryId;

    private String name;

    private String description;

    private int maxMembers;

    private Boolean isPublic;

    public GroupCreateRequest(List<Long> groupCategoryId, String name, String description,
                              int maxMembers, Boolean isPublic) {
        this.groupCategoryId = groupCategoryId;
        this.name = name;
        this.description = description;
        this.maxMembers = maxMembers;
        this.isPublic = isPublic;
    }

    public List<Long> getGroupCategoryId() {
        return groupCategoryId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }
}