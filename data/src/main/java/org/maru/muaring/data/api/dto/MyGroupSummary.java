package org.maru.muaring.data.api.dto;

public class MyGroupSummary {

    private Long groupId;
    private String name;
    private String description;
    private Integer memberCount;
    private Boolean isPublic;
    private String myRole;
    private String createdAt;
    private String imageUrl;

    public Long getGroupId() { return groupId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Integer getMemberCount() { return memberCount; }
    public Boolean getIsPublic() { return isPublic; }
    public String getMyRole() { return myRole; }
    public String getCreatedAt() { return createdAt; }
    public String getImageUrl() { return imageUrl; }
}
