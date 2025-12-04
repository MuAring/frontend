package org.maru.muaring.data.repository;

import androidx.lifecycle.LiveData;

import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.dto.MusicHistoryResponse;

import java.util.List;

public interface HistoryRepository {

    LiveData<Resource<List<MusicHistoryResponse>>> getMemberHistory(
            Integer year, Integer month, int page
    );

    public LiveData<Resource<List<MusicHistoryResponse>>> getGroupHistory(
            Long groupId, Integer year, Integer month, int page
    );
}
