package org.maru.muaring.data.repository;

import org.maru.muaring.core.common.Callback;
import org.maru.muaring.data.api.dto.SpotifyTrackResponse;

import java.util.List;

public interface UploadRepository {
    void searchMusic(String query, Callback<List<SpotifyTrackResponse>> callback);

}
