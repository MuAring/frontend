package org.maru.muaring.data.api.dto;

public class SpotifyTrackResponse {
    private String spotifyId;
    private String name;
    private String artistName;
    private String albumName;
    private String albumImgUrl;
    private int popularity;

    public String getName() { return name; }
    public String getArtistName() { return artistName; }
    public String getAlbumImgUrl() { return albumImgUrl; }
}
