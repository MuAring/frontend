package org.maru.muaring.data.api.dto;

import java.util.List;

public class ExportRequest {
    private List<Long> musicIds;

    public ExportRequest(List<Long> musicIds) {
        this.musicIds = musicIds;
    }
}
