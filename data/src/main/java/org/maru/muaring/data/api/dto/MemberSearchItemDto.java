package org.maru.muaring.data.api.dto;

public class MemberSearchItemDto {

    private Long memberId;
    private String nickname;
    private String profileImageUrl;
    private Boolean isPublic;
    private Boolean isFollowing;

    private String todayMusicName;
    private String todayMusicArtistName;

    public Long getMemberId() { return memberId; }
    public String getNickname() { return nickname; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public Boolean getIsPublic() { return isPublic; }
    public Boolean getIsFollowing() { return isFollowing; }
    public void setIsFollowing(Boolean following) { this.isFollowing = following; }
    public String getTodayMusicName() { return todayMusicName; }
    public String getTodayMusicArtistName() { return todayMusicArtistName; }
}
