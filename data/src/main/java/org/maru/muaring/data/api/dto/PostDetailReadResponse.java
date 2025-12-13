package org.maru.muaring.data.api.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class PostDetailReadResponse {

    // 게시물 정보
    private Long postId;
    private String content;
    private Integer likeCount;
    private Integer commentCount;
    @SerializedName("liked")
    private boolean isLiked;
    private String createdAt;

    // 작성자
    private AuthorDto author;

    // 음악
    private MusicDto music;

    @Getter
    public static class AuthorDto {
        private Long memberId;
        private String nickname;
        private String profileImageUrl;
    }

    @Getter
    public static class MusicDto {
        private Long musicId;
        private String spotifyId;
        private String name;
        private String artistName;
        private String albumName;
        private String albumImgUrl;
        private Integer durationMs;
        private String previewUrl;
    }
}