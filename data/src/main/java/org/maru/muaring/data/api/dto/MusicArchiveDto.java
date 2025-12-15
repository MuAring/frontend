package org.maru.muaring.data.api.dto;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDateTime;

public class MusicArchiveDto {
    @SerializedName("postId")
    private Long postId;

    @SerializedName("musicId")
    private Long musicId;

    @SerializedName("title")
    private String title;

    @SerializedName("artist")
    private String artist;

    @SerializedName("albumImage")
    private String albumImage;

    @SerializedName("albumName")
    private String albumName;

    @SerializedName("firstPostedAt")
    private String firstPostedAt; // ISO 8601 형식

    // Getters
    public Long getPostId() { return postId; }
    public Long getMusicId() { return musicId; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getAlbumImage() { return albumImage; }
    public String getAlbumName() { return albumName; }
    public String getFirstPostedAt() { return firstPostedAt; }

    // Setters
    public void setPostId(Long postId) { this.postId = postId; }
    public void setMusicId(Long musicId) { this.musicId = musicId; }
    public void setTitle(String title) { this.title = title; }
    public void setArtist(String artist) { this.artist = artist; }
    public void setAlbumImage(String albumImage) { this.albumImage = albumImage; }
    public void setAlbumName(String albumName) { this.albumName = albumName; }
    public void setFirstPostedAt(String firstPostedAt) { this.firstPostedAt = firstPostedAt; }
}