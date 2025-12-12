package org.maru.muaring.data.api.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GroupCreateResponse {

    private Long groupId;

    private Long adminId;

    private List<Long> groupCategoryId;

    private String name;

    private String description;

    private int memberCount;

    private int maxMembers;

    private Boolean isPublic;

    public Long getGroupId() {
        return groupId;
    }

    public Long getAdminId() {
        return adminId;
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

    public int getMemberCount() {
        return memberCount;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }
}