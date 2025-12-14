package org.maru.muaring.feature.nearby.ui.model;

public class Music {
    private Long memberId;
    private String profileImageUrl;
    private String title;
    private String artist;
    private String albumImageUrl;

    public Music(Long memberId, String profileImageUrl, String title, String artist, String albumImageUrl) {
        this.memberId = memberId;
        this.profileImageUrl = profileImageUrl;
        this.title = title;
        this.artist = artist;
        this.albumImageUrl = albumImageUrl;
    }

    public Long getMemberId() { return memberId; }

    public String getProfileImageUrl() { return profileImageUrl; }

    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getAlbumImageUrl() { return albumImageUrl; }
}
