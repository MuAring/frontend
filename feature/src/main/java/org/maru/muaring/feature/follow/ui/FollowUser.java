package org.maru.muaring.feature.follow.ui;

public class FollowUser {
    private Long memberId;
    private String name;
    private String profileImage;
    private String followStatus;
    private String musicTitle;
    private String musicArtist;

    public FollowUser(Long memberId, String name, String profileImage, String followStatus, String musicTitle, String musicArtist) {
        this.memberId = memberId;
        this.name = name;
        this.profileImage = profileImage;
        this.followStatus = followStatus;
        this.musicTitle = musicTitle;
        this.musicArtist = musicArtist;
    }

    public Long getMemberId() { return memberId; }

    public String getName() { return name; }
    public String getProfileImage() {
        return profileImage;
    }
    public String getFollowStatus() {
        return followStatus;
    }

    public void setFollowStatus(String followStatus) {
        this.followStatus = followStatus;
    }

    public String getMusicInfo() {
        return musicTitle + " - " + musicArtist;
    }
}
