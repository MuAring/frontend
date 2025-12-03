package org.maru.muaring.feature.history.ui.model;

public class MusicHistoryItem {

    private final int dayNumber; // createdAt에서 day 추출해서 넣기
    private final Long postId;
    private final Long musicId;
    private final String title;
    private final String artist;
    private final String albumImageUrl;

    public MusicHistoryItem(int dayNumber,
                            Long postId,
                            Long musicId,
                            String title,
                            String artist,
                            String albumImageUrl) {
        this.dayNumber = dayNumber;
        this.postId = postId;
        this.musicId = musicId;
        this.title = title;
        this.artist = artist;
        this.albumImageUrl = albumImageUrl;
    }

    public int getDayNumber() { return dayNumber; }
    public Long getPostId() { return postId; }
    public Long getMusicId() { return musicId; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getAlbumImageUrl() { return albumImageUrl; }
}
