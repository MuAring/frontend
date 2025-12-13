package org.maru.muaring.data.api.dto;

import java.util.List;

public class GroupRecommendItemDto {

    private Long groupId;
    private String imgUrl;
    private String name;
    private List<String> categoryNames;
    private Boolean isJoined;

    public Long getGroupId() {
        return groupId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getName() {
        return name;
    }

    public List<String> getCategoryNames() {
        return categoryNames;
    }

    public Boolean getIsJoined() {
        return isJoined;
    }
}
