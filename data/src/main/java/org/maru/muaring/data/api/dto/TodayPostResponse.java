package org.maru.muaring.data.api.dto;

import com.google.gson.annotations.SerializedName;

public class TodayPostResponse {

    @SerializedName("postId")
    private Long postId;

    @SerializedName("musicId")
    private Long musicId;

    @SerializedName("musicName")
    private String musicName;

    @SerializedName("artistName")
    private String artistName;

    @SerializedName("albumImageUrl")
    private String albumImageUrl;

    @SerializedName("likeCount")
    private Integer likeCount;

    @SerializedName("commentCount")
    private Integer commentCount;

    // Getters
    public Long getPostId() {
        return postId;
    }

    public Long getMusicId() {
        return musicId;
    }

    public String getMusicName() {
        return musicName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getAlbumImageUrl() {
        return albumImageUrl;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }
}