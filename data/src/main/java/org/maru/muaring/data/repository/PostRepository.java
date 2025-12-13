package org.maru.muaring.data.repository;

import androidx.lifecycle.LiveData;
import org.maru.muaring.core.common.Callback;
import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.dto.MusicPostFeedResponse;
import org.maru.muaring.data.api.dto.TodayPostResponse;
import org.maru.muaring.data.api.dto.PostDetailReadResponse;
import java.util.List;

public interface PostRepository {

    LiveData<Resource<List<MusicPostFeedResponse>>> getTodayPostsForMe();
    LiveData<Resource<List<MusicPostFeedResponse>>> getTodayPostsForGroup(Long groupId);
    // 프로필의 오늘 공유한 음악 조회
    LiveData<Resource<TodayPostResponse>> getTodayPostByMember(Long memberId);
    void getPostDetail(Long postId, Callback<PostDetailReadResponse> callback);
}