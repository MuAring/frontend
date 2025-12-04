package org.maru.muaring.feature.group.ui;

import org.maru.muaring.data.api.dto.InvitePreviewResponse;

public class InvitePreviewData {
    private final Long groupId;
    private final String groupName;
    private final String groupDescription;
    private final String groupImageUrl;
    private final int memberCount;
    private final int maxMembers;
    private final boolean isExpired;
    private final boolean isUsable;

    private InvitePreviewData(Long groupId, String groupName, String groupDescription,
                              String groupImageUrl, int memberCount, int maxMembers,
                              boolean isExpired, boolean isUsable) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.groupImageUrl = groupImageUrl;
        this.memberCount = memberCount;
        this.maxMembers = maxMembers;
        this.isExpired = isExpired;
        this.isUsable = isUsable;
    }

    public static InvitePreviewData from(InvitePreviewResponse response) {
        return new InvitePreviewData(
                response.getGroupId(),
                response.getGroupName(),
                response.getGroupDescription(),
                response.getGroupImage(),
                response.getCurrentMembers(),
                response.getMaxMembers(),
                response.isExpired(),
                response.isUsable()
        );
    }

    // Getters
    public Long getGroupId() { return groupId; }
    public String getGroupName() { return groupName; }
    public String getGroupDescription() { return groupDescription; }
    public String getGroupImageUrl() { return groupImageUrl; }
    public int getMemberCount() { return memberCount; }
    public int getMaxMembers() { return maxMembers; }
    public boolean isExpired() { return isExpired; }
    public boolean isUsable() { return isUsable; }
}