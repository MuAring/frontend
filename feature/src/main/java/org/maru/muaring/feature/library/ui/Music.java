package org.maru.muaring.feature.library.ui;

public class Music {
    private Long id;
    private String title;
    private String artist;
    private String albumImage;

    public Music(Long id, String title, String artist, String albumImage) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.albumImage = albumImage;
    }

    public Long getId() { return id; }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbumImage() {
        return albumImage;
    }
}
