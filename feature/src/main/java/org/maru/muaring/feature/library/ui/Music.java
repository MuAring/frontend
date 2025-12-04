package org.maru.muaring.feature.library.ui;

public class Music {
    private Long id;
    private String title;
    private String artist;
    private int albumImageRes;

    public Music(Long id, String title, String artist, int albumImageRes) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.albumImageRes = albumImageRes;
    }

    public Long getId() { return id; }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public int getAlbumImageRes() {
        return albumImageRes;
    }
}
