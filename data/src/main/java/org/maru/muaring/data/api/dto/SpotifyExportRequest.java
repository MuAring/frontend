package org.maru.muaring.data.api.dto;

import java.util.List;

public class SpotifyExportRequest {
    private String spotifyAccessToken;
    private List<Long> musicIds;

    public SpotifyExportRequest(String spotifyAccessToken, List<Long> musicIds) {
        this.spotifyAccessToken = spotifyAccessToken;
        this.musicIds = musicIds;
    }
}
