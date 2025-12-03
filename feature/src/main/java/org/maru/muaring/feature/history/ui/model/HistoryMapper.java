package org.maru.muaring.feature.history.ui.model;

import org.maru.muaring.data.api.dto.MusicHistoryResponse;

import java.util.ArrayList;
import java.util.List;

public class HistoryMapper {

    public static int extractDay(String createdAt) {
        if (createdAt == null || createdAt.length() < 10) return 1;
        // "yyyy-MM-dd..." 에서 일(day) 부분만 잘라서 int로
        try {
            String dayStr = createdAt.substring(8, 10);
            return Integer.parseInt(dayStr);
        } catch (Exception e) {
            return 1;
        }
    }

    public static MusicHistoryItem toItem(MusicHistoryResponse dto) {
        int day = extractDay(dto.getCreatedAt());

        return new MusicHistoryItem(
                day,
                dto.getPostId(),
                dto.getMusicId(),
                dto.getTitle(),
                dto.getArtist(),
                dto.getAlbumImage()
        );
    }

    public static List<MusicHistoryItem> toItemList(List<MusicHistoryResponse> list) {
        List<MusicHistoryItem> result = new ArrayList<>();
        if (list == null) return result;
        for (MusicHistoryResponse dto : list) {
            result.add(toItem(dto));
        }
        return result;
    }
}
