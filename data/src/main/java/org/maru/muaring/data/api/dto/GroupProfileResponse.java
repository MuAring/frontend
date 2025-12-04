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
//    private Integer level; // 나중에 백에서 추가할 필드라고 가정

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

//    public Integer getLevel() {
//        return level;
//    }
}
