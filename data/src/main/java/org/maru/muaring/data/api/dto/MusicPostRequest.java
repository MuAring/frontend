package org.maru.muaring.data.api.dto;

public class MusicPostRequest {
    private Long groupId;
    private String spotifyId;
    private String content;

    public MusicPostRequest(Long groupId, String spotifyId, String content) {
        this.groupId = groupId;
        this.spotifyId = spotifyId;
        this.content = content;
    }

    public Long getGroupId() { return groupId; }
    public String getSpotifyId() { return spotifyId; }
    public String getContent() { return content; }
}