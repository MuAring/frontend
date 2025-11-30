package org.maru.muaring.data.repository;

import androidx.lifecycle.LiveData;

import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.dto.MusicPostFeedResponse;

import java.util.List;

public interface PostRepository {

    LiveData<Resource<List<MusicPostFeedResponse>>> getTodayPostsForMe();

    LiveData<Resource<List<MusicPostFeedResponse>>> getTodayPostsForGroup(Long groupId);
}