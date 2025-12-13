package org.maru.muaring.data.api.dto;

public class LibraryMusicDTO {

    private Long libraryId;
    private Long musicId;
    private String title;
    private String artist;
    private String albumImage;
    private String createdAt; // LocalDateTime -> String

    public Long getLibraryId() { return libraryId; }
    public Long getMusicId() { return musicId; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getAlbumImage() { return albumImage; }
    public String getCreatedAt() { return createdAt; }
}
