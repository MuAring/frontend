package org.maru.muaring.feature.library.ui;

public class Music {
    private Long libraryId;
    private Long musicId;
    private String title;
    private String artist;
    private String albumImage;

    public Music(Long libraryId, Long musicId, String title, String artist, String albumImage) {
       this.libraryId = libraryId;
        this.musicId = musicId;
        this.title = title;
        this.artist = artist;
        this.albumImage = albumImage;
    }

    public Long getLibraryId() { return libraryId; }

    public Long getMusicId() { return musicId; }

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
