package org.maru.muaring.data.repository;

import androidx.lifecycle.LiveData;

import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.dto.DailyTopMusicResponse;

public interface StatsRepository {
    LiveData<Resource<DailyTopMusicResponse>> getYesterdayTop3();
    LiveData<Resource<DailyTopMusicResponse>> getLast7DaysTop3();
}
