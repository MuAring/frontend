package org.maru.muaring.data.repository;

import androidx.lifecycle.LiveData;

import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.dto.LibraryMusicDTO;

import java.util.List;

public interface LibraryRepository {

    LiveData<Resource<LibraryMusicDTO>> addMusicToLibrary(Long musicId, String category);

    LiveData<Resource<Void>> deleteMusicFromLibrary(List<Long> musicIds);
}
