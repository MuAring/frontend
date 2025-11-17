package org.maru.muaring.feature.nearby.ui.model;

public class Music {
    private String title;
    private String artist;
    private int albumImageRes;

    public Music(String title, String artist, int albumImageRes) {
        this.title = title;
        this.artist = artist;
        this.albumImageRes = albumImageRes;
    }

    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public int getAlbumImageRes() { return albumImageRes; }
}
