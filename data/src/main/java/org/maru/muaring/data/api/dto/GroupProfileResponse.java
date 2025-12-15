package org.maru.muaring.data.api.dto;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDateTime;
import java.util.List;

public class GroupProfileResponse {

    @SerializedName("groupId")
    private Long groupId;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("groupCategories")
    private List<String> groupCategories;

    @SerializedName("totalMusicCount")
    private Integer totalMusicCount;

    @SerializedName("totalPostCount")
    private Integer totalPostCount;

    @SerializedName("memberCount")
    private Integer memberCount;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("level")
    private Integer level;

    @SerializedName("exp")
    private Long exp;

    @SerializedName("nextLevelExp")
    private Long nextLevelExp;

    @SerializedName("remainingExpToNext")
    private Long remainingExpToNext;

    // 그룹 가입 여부
    @SerializedName("isJoined")
    private Boolean isJoined;

    // Getters
    public Long getGroupId() { return groupId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<String> getGroupCategories() { return groupCategories; }
    public Integer getTotalMusicCount() { return totalMusicCount; }
    public Integer getTotalPostCount() { return totalPostCount; }
    public Integer getMemberCount() { return memberCount; }
    public String getImageUrl() { return imageUrl; }
    public String getCreatedAt() { return createdAt; }
    public Integer getLevel() { return level; }
    public Long getExp() { return exp; }
    public Long getNextLevelExp() { return nextLevelExp; }
    public Long getRemainingExpToNext() { return remainingExpToNext; }
    public Boolean getIsJoined() { return isJoined; }
}