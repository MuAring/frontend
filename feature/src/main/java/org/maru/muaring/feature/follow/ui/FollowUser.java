package org.maru.muaring.feature.follow.ui;

public class FollowUser {
    private String name;
    private String profileImage;
    private String followStatus;
    private String title;
    private String artist;

    public FollowUser(String name, String profileImage, String followStatus) {
        this.name = name;
        this.profileImage = profileImage;
        this.followStatus = followStatus;
    }

    public String getName() { return name; }
    public String getProfileImage() {
        return profileImage;
    }
    public String getFollowStatus() {
        return followStatus;
    }
    public String getMusicInfo() {
        return title + " - " + artist;
    }
}
