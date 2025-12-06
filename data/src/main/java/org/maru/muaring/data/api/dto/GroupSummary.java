 package org.maru.muaring.data.api.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GroupSummary {

    @SerializedName("groupId")
    private Long groupId;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("categoryNames")
    private List<String> categoryNames;

    @SerializedName("memberCount")
    private int memberCount;

    @SerializedName("maxMembers")
    private int maxMembers;

    @SerializedName("isPublic")
    private Boolean isPublic;

    // 내가 가입했는지 여부
    @SerializedName("isJoined")
    private Boolean isJoined;

    // 그룹 프로필 이미지 URL
    @SerializedName("imageUrl")
    private String imageUrl;

    public Long getGroupId() {
        return groupId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getCategoryNames() {
        return categoryNames;
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

    public Boolean getIsJoined() {
        return isJoined;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
