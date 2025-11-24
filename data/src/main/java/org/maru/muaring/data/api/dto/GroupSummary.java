package org.maru.muaring.data.api.dto;

import java.util.List;

public class GroupSummary {

    private Long groupId;
    private String name;
    private String description;
    private List<String> categoryNames;  // 이름 리스트로 변경
    private int memberCount;
    private int maxMembers;
    private Boolean isPublic;
    private Boolean isJoined;            // 내가 가입 중인지 여부

    public Long getGroupId() { return groupId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<String> getCategoryNames() { return categoryNames; }
    public int getMemberCount() { return memberCount; }
    public int getMaxMembers() { return maxMembers; }
    public Boolean getIsPublic() { return isPublic; }
    public Boolean getIsJoined() { return isJoined; }
}
