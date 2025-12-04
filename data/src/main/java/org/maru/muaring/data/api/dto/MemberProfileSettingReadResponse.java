package org.maru.muaring.data.api.dto;

public class MemberProfileSettingReadResponse {
    public String imageUrl;
    public String nickname;
    public Boolean isAccountPublic;
    public Boolean isDiscoveryEnabled;

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