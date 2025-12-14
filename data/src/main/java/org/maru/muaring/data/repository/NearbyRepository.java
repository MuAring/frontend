package org.maru.muaring.data.repository;

import androidx.lifecycle.LiveData;

import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.dto.TodayNearbyMusicDTO;

import java.util.List;

public interface NearbyRepository {

    LiveData<Resource<List<TodayNearbyMusicDTO>>> getTodayNearbyMusic(
            double lat,
            double lng,
            double radiusKm
    );
}

