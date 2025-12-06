package org.maru.muaring.data.api.dto;

import com.google.gson.annotations.SerializedName;
//import java.time.LocalDateTime;
import java.util.List;

public class MyGroupSummary {

    @SerializedName("groupId")
    private Long groupId;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("categoryNames")
    private List<String> categoryNames;

    @SerializedName("memberCount")
    private Integer memberCount;

    @SerializedName("isPublic")
    private Boolean isPublic;

    @SerializedName("myRole")
    private String myRole;

    // TODO: Gson은 java.time.LocalDateTime을 그냥은 못 파싱해서,
    // 나중에 써야 할 때 수정해서 쓰기...? (자꾸 터져서 막아둠)
    @SerializedName("createdAt")
    private String createdAt;

    // 새로 추가된 이미지 URL
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

    public Integer getMemberCount() {
        return memberCount;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public String getMyRole() {
        return myRole;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}