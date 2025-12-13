package org.maru.muaring.feature.follow.ui;

public class FollowUser {
    private String name;
    private String profileImage;
    private String followStatus;
    private String musicTitle;
    private String musicArtist;

    public FollowUser(String name, String profileImage, String followStatus, String musicTitle, String musicArtist) {
        this.name = name;
        this.profileImage = profileImage;
        this.followStatus = followStatus;
        this.musicTitle = musicTitle;
        this.musicArtist = musicArtist;
    }

    public String getName() { return name; }
    public String getProfileImage() {
        return profileImage;
    }
    public String getFollowStatus() {
        return followStatus;
    }
    public String getMusicInfo() {
        return musicTitle + " - " + musicArtist;
    }
}
