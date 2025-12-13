package org.maru.muaring.data.api.dto;

import com.google.gson.annotations.SerializedName;

public class TodayMusicPostResponse {

    // 게시글 정보
    @SerializedName("postId")
    private Long postId;

    @SerializedName("groupId")
    private Long groupId;

    // 작성자 정보
    @SerializedName("memberId")
    private Long memberId;

    @SerializedName("memberNickname")
    private String memberNickname;

    @SerializedName("memberProfileImageUrl")
    private String memberProfileImageUrl;

    // 음악 정보
    @SerializedName("musicId")
    private Long musicId;

    @SerializedName("spotifyId")
    private String spotifyId;

    @SerializedName("musicName")
    private String musicName;

    @SerializedName("artistId")
    private String artistId;

    @SerializedName("artistName")
    private String artistName;

    @SerializedName("albumName")
    private String albumName;

    @SerializedName("albumImgUrl")
    private String albumImgUrl;

    @SerializedName("durationMs")
    private Integer durationMs;

    // 게시글 메타
    @SerializedName("isProfile")
    private boolean isProfile;

    @SerializedName("content")
    private String content;

    @SerializedName("likeCount")
    private Integer likeCount;

    @SerializedName("commentCount")
    private Integer commentCount;

    @SerializedName("createdAt")
    private String createdAt;

    // Getters
    public Long getPostId() {
        return postId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getMemberNickname() {
        return memberNickname;
    }

    public String getMemberProfileImageUrl() {
        return memberProfileImageUrl;
    }

    public Long getMusicId() {
        return musicId;
    }

    public String getSpotifyId() {
        return spotifyId;
    }

    public String getMusicName() {
        return musicName;
    }

    public String getArtistId() {
        return artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getAlbumImgUrl() {
        return albumImgUrl;
    }

    public Integer getDurationMs() {
        return durationMs;
    }

    public boolean isProfile() {
        return isProfile;
    }

    public String getContent() {
        return content;
    }

    public Integer getLikeCount() {
        return likeCount != null ? likeCount : 0;
    }

    public Integer getCommentCount() {
        return commentCount != null ? commentCount : 0;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}