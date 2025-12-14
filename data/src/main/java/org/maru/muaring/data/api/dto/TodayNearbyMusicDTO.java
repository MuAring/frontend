package org.maru.muaring.data.api.dto;

public class TodayNearbyMusicDTO {
    private Long memberId;
    private String profileImageUrl;
    private String musicName;
    private String artistName;
    private String albumImageUrl;

    public Long getMemberId(){
        return memberId;
    }

    public String getProfileImageUrl(){
        return profileImageUrl;
    }

    public String getMusicName(){
        return musicName;
    }

    public String getArtistName(){
        return artistName;
    }
    public String getAlbumImageUrl(){
        return albumImageUrl;
    }
}
