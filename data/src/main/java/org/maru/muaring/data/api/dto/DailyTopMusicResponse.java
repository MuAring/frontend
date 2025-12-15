package org.maru.muaring.data.api.dto;

import java.util.List;

public class DailyTopMusicResponse {

    private String period;     // "yesterday", "last7days"
    private String startDate;  // yyyy-MM-dd
    private String endDate;    // yyyy-MM-dd
    private List<Item> musics;

    public DailyTopMusicResponse() { }

    public DailyTopMusicResponse(String period, String startDate, String endDate, List<Item> musics) {
        this.period = period;
        this.startDate = startDate;
        this.endDate = endDate;
        this.musics = musics;
    }

    public String getPeriod() { return period; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public List<Item> getMusics() { return musics; }

    public static class Item {
        private String title;
        private String artistName;
        private String albumImgUrl;
        private long shareCount;

        public Item() { }

        public Item(String title, String artistName, String albumImgUrl, long shareCount) {
            this.title = title;
            this.artistName = artistName;
            this.albumImgUrl = albumImgUrl;
            this.shareCount = shareCount;
        }

        public String getTitle() { return title; }
        public String getArtistName() { return artistName; }
        public String getAlbumImgUrl() { return albumImgUrl; }
        public long getShareCount() { return shareCount; }
    }
}
