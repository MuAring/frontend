package org.maru.muaring.feature.post.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import org.maru.muaring.core.common.Callback;
import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.dto.LikeResponseDTO;
import org.maru.muaring.data.api.dto.PostDetailReadResponse;
import org.maru.muaring.data.repository.LikeRepository;
import org.maru.muaring.data.repository.PostRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import javax.inject.Inject;

@HiltViewModel
public class PostDetailReadViewModel extends ViewModel {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final MutableLiveData<Resource<PostDetailReadResponse>> postDetail = new MutableLiveData<>();

    @Inject
    public PostDetailReadViewModel(PostRepository postRepository, LikeRepository likeRepository) {
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        postDetail.setValue(Resource.loading(null));
    }

    public LiveData<Resource<PostDetailReadResponse>> getPostDetail(Long postId) {
        loadPostDetail(postId);
        return postDetail;
    }

    private void loadPostDetail(Long postId) {
        postRepository.getPostDetail(postId, new Callback<PostDetailReadResponse>() {
            @Override
            public void onSuccess(PostDetailReadResponse response) {
                postDetail.postValue(Resource.success(response));
            }

            @Override
            public void onError(Exception e) {
                postDetail.postValue(Resource.error("프로필 정보를 불러오지 못했어요 🥲", null));
            }
        });
    }

    public LiveData<Resource<LikeResponseDTO>> toggleLike(Long postId) {
        return likeRepository.toggleLike(postId);
    }
}