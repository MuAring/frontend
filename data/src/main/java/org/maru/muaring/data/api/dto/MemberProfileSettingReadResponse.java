package org.maru.muaring.data.api.dto;

public class MemberProfileSettingReadResponse {
    private String imageUrl;
    private String nickname;
    private Boolean isAccountPublic;
    private Boolean isDiscoveryEnabled;

    public String getImageUrl() {
        return imageUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public Boolean getIsAccountPublic() {
        return isAccountPublic;
    }

    public Boolean getIsDiscoveryEnabled() {
        return isDiscoveryEnabled;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public void setIsAccountPublic(Boolean isAccountPublic) {
        this.isAccountPublic = isAccountPublic;
    }
    public void setIsDiscoveryEnabled(Boolean isDiscoveryEnabled) {
        this.isDiscoveryEnabled = isDiscoveryEnabled;
    }
}