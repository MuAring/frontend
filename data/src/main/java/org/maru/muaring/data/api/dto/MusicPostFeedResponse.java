package org.maru.muaring.data.api.dto;

import com.google.gson.annotations.SerializedName;

public class MusicPostFeedResponse {

    private Long postId;
    private Long groupId;

    private Long memberId;
    private String memberNickname;
    private String memberProfileImageUrl;

    private Long musicId;
    private String spotifyId;
    private String musicName;
    private String artistId;
    private String artistName;
    private String albumName;
    private String albumImgUrl;
    private Integer durationMs;

    // 서버: profile
    @SerializedName(value = "isProfile", alternate = {"profile"})
    private boolean isProfile;
    private String content;
    private Integer likeCount;

    // 서버: liked
    @SerializedName(value = "isLiked", alternate = {"liked"})
    private boolean isLiked;

    private Integer commentCount;

    // 서버: inLibrary
    @SerializedName(value = "isInLibrary", alternate = {"inLibrary"})
    private boolean isInLibrary;
    private String createdAt; // LocalDateTime → 문자열

    // getter
    public Long getPostId() { return postId; }
    public Long getGroupId() { return groupId; }
    public Long getMemberId() { return memberId; }
    public String getMemberNickname() { return memberNickname; }
    public String getMemberProfileImageUrl() { return memberProfileImageUrl; }
    public Long getMusicId() { return musicId; }
    public String getSpotifyId() { return spotifyId; }
    public String getMusicName() { return musicName; }
    public String getArtistId() { return artistId; }
    public String getArtistName() { return artistName; }
    public String getAlbumName() { return albumName; }
    public String getAlbumImgUrl() { return albumImgUrl; }
    public Integer getDurationMs() { return durationMs; }
    public boolean isProfile() { return isProfile; }
    public String getContent() { return content; }
    public Integer getLikeCount() { return likeCount; }
    public boolean getIsLiked() { return isLiked; }
    public Integer getCommentCount() { return commentCount; }
    public boolean isInLibrary() {
        return isInLibrary;
    }
    public String getCreatedAt() { return createdAt; }

    // setter
    public void setLikeCount(Integer newLikeCount) {
        likeCount = newLikeCount;
    }

    public void setIsLiked(boolean newIsLiked) {
        isLiked = newIsLiked;
    }

    public void setInLibrary(boolean inLibrary) {
        isInLibrary = inLibrary;
    }

}
