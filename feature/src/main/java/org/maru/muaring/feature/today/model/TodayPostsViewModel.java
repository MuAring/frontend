package org.maru.muaring.feature.today.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.dto.MusicPostFeedResponse;
import org.maru.muaring.data.repository.PostRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TodayPostsViewModel extends ViewModel {

    private final PostRepository postRepository;

    private final MediatorLiveData<Resource<List<MusicPostFeedResponse>>> _posts =
            new MediatorLiveData<>();
    public LiveData<Resource<List<MusicPostFeedResponse>>> posts = _posts;

    @Inject
    public TodayPostsViewModel(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public void loadForMe() {
        _posts.setValue(Resource.loading(null));
        LiveData<Resource<List<MusicPostFeedResponse>>> source =
                postRepository.getTodayPostsForMe();
        _posts.addSource(source, res -> {
            _posts.setValue(res);
            _posts.removeSource(source);
        });
    }

    public void loadForGroup(Long groupId) {
        _posts.setValue(Resource.loading(null));
        LiveData<Resource<List<MusicPostFeedResponse>>> source =
                postRepository.getTodayPostsForGroup(groupId);
        _posts.addSource(source, res -> {
            _posts.setValue(res);
            _posts.removeSource(source);
        });
    }
}