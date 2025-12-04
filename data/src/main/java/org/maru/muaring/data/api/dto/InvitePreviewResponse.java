package org.maru.muaring.data.api.dto;

import com.google.gson.annotations.SerializedName;

public class InvitePreviewResponse {

    @SerializedName("groupId")
    private Long groupId;

    @SerializedName("groupName")
    private String groupName;

    @SerializedName("groupDescription")
    private String groupDescription;

    @SerializedName("groupImage")
    private String groupImage;

    @SerializedName("currentMembers")
    private Integer currentMembers;

    @SerializedName("maxMembers")
    private Integer maxMembers;

    @SerializedName("isExpired")
    private Boolean isExpired;

    @SerializedName("isUsable")
    private Boolean isUsable;

    // Getters
    public Long getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public String getGroupImage() {
        return groupImage;
    }

    public Integer getCurrentMembers() {
        return currentMembers;
    }

    public Integer getMaxMembers() {
        return maxMembers;
    }

    public Boolean isExpired() {
        return isExpired;
    }

    public Boolean isUsable() {
        return isUsable;
    }
}