package org.maru.muaring.feature.follow.ui;

public class FollowUser {
    private String name;
    private String title;
    private String artist;

    public FollowUser(String name, String title, String artist) {
        this.name = name;
        this.title = title;
        this.artist = artist;
    }

    public String getName() { return name; }
    public String getMusicInfo() {
        return title + " - " + artist;
    }
}
