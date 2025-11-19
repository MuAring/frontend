package org.maru.muaring.data.model;

import com.google.gson.annotations.SerializedName;

public class GroupInviteResponse {

    @SerializedName("inviteId")
    private Long inviteId;

    @SerializedName("inviteUrl")
    private String inviteUrl;

    @SerializedName("inviteToken")
    private String inviteToken;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("expiresAt")
    private String expiresAt;

    @SerializedName("inviterName")
    private String inviterName;

    // Getter 메서드들
    public Long getInviteId() {
        return inviteId;
    }

    public String getInviteUrl() {
        return inviteUrl;
    }

    public String getInviteToken() {
        return inviteToken;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public String getInviterName() {
        return inviterName;
    }
}