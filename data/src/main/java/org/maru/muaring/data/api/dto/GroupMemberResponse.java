package org.maru.muaring.data.api.dto;

import com.google.gson.annotations.SerializedName;

public class GroupMemberResponse {

    @SerializedName("memberId")
    private Long memberId;

    @SerializedName("nickname")
    private String nickname;

    @SerializedName("profileImageUrl")
    private String profileImageUrl;

    @SerializedName("role")
    private String role;

    @SerializedName("joinedAt")
    private String joinedAt;

    @SerializedName("recentMusic")
    private RecentMusicDto recentMusic;

    // Getters
    public Long getMemberId() {
        return memberId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getRole() {
        return role;
    }

    public String getJoinedAt() {
        return joinedAt;
    }

    public RecentMusicDto getRecentMusic() {
        return recentMusic;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    public static class RecentMusicDto {
        @SerializedName("musicId")
        private Long musicId;

        @SerializedName("musicName")
        private String musicName;

        @SerializedName("artistName")
        private String artistName;

        @SerializedName("albumImgUrl")
        private String albumImgUrl;

        @SerializedName("postId")
        private Long postId;

        // Getters
        public Long getMusicId() {
            return musicId;
        }

        public String getMusicName() {
            return musicName;
        }

        public String getArtistName() {
            return artistName;
        }

        public String getAlbumImgUrl() {
            return albumImgUrl;
        }

        public Long getPostId() {
            return postId;
        }

        public String getDisplayText() {
            if (musicName != null && artistName != null) {
                return musicName + " - " + artistName;
            } else if (musicName != null) {
                return musicName;
            }
            return "";
        }
    }
}