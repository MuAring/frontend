package org.maru.muaring.data.api.dto;

public class MemberSettingsResponse {
    private String imageUrl;
    private String nickname;
    private Boolean isAccountPublic;
    private Boolean isDiscoveryEnabled;

    public String getImageUrl() { return imageUrl; }
    public String getNickname() { return nickname; }
    public Boolean getIsAccountPublic() { return isAccountPublic; }
    public Boolean getIsDiscoveryEnabled() { return isDiscoveryEnabled; }
}
