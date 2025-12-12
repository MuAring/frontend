package org.maru.muaring.data.api.dto;

import java.util.List;

public class GroupProfileResponse {

    private Long groupId;
    private String name;
    private String description;
    private List<String> groupCategories;
    private Integer totalMusicCount;
    private Integer totalPostCount;
    private Integer memberCount;
    private String imageUrl;
    private String createdAt;

    // 레벨 관련 필드 추가
    private Integer level;               // 현재 레벨 (1~5)
    private Long exp;                    // 현재 누적 EXP
    private Long nextLevelExp;           // 다음 레벨이 요구하는 총 EXP (없으면 null)
    private Long remainingExpToNext;     // 다음 레벨까지 남은 EXP (최대 레벨이면 0)


    public Long getGroupId() {
        return groupId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getGroupCategories() {
        return groupCategories;
    }

    public Integer getTotalMusicCount() {
        return totalMusicCount;
    }

    public Integer getTotalPostCount() {
        return totalPostCount;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public Integer getLevel() {
        return level;
    }

    public Long getExp() {
        return exp;
    }

    public Long getNextLevelExp() {
        return nextLevelExp;
    }

    public Long getRemainingExpToNext() {
        return remainingExpToNext;
    }
}
