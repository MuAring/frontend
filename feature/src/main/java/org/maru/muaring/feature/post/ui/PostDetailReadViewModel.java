package org.maru.muaring.feature.post.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import org.maru.muaring.core.common.Callback;
import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.dto.CommentCreateRequest;
import org.maru.muaring.data.api.dto.CommentReadResponse;
import org.maru.muaring.data.api.dto.CommentResponse;
import org.maru.muaring.data.api.dto.LibraryMusicDTO;
import org.maru.muaring.data.api.dto.LikeResponseDTO;
import org.maru.muaring.data.api.dto.PostDetailReadResponse;
import org.maru.muaring.data.repository.LibraryRepository;
import org.maru.muaring.data.repository.LikeRepository;
import org.maru.muaring.data.repository.PostRepository;
import java.util.List;
import dagger.hilt.android.lifecycle.HiltViewModel;
import javax.annotation.Nullable;
import javax.inject.Inject;

@HiltViewModel
public class PostDetailReadViewModel extends ViewModel {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final LibraryRepository libraryRepository;
    private final MutableLiveData<Resource<PostDetailReadResponse>> postDetail = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<CommentReadResponse>>> comments = new MutableLiveData<>();
    private final MutableLiveData<Resource<CommentResponse>> commentResult = new MutableLiveData<>();

    @Inject
    public PostDetailReadViewModel(PostRepository postRepository, LikeRepository likeRepository, LibraryRepository libraryRepository) {
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.libraryRepository = libraryRepository;
        postDetail.setValue(Resource.loading(null));
        comments.setValue(Resource.loading(null));
    }

    public LiveData<Resource<PostDetailReadResponse>> getPostDetail(Long postId) {
        loadPostDetail(postId);
        return postDetail;
    }

    public LiveData<Resource<List<CommentReadResponse>>> getComments(Long postId) {
        loadComments(postId);
        return comments;
    }

    public LiveData<Resource<CommentResponse>> getCommentResult() {
        return commentResult;
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

    public LiveData<Resource<LibraryMusicDTO>> addMusicToLibrary(
            Long musicId,
            @Nullable String category
    ) {
        return libraryRepository.addMusicToLibrary(musicId, category);
    }

    public void loadComments(Long postId) {
        postRepository.getComments(postId, new Callback<List<CommentReadResponse>>() {
            @Override
            public void onSuccess(List<CommentReadResponse> response) {
                comments.postValue(Resource.success(response));
            }

            @Override
            public void onError(Exception e) {
                postDetail.postValue(Resource.error("댓글을 불러오지 못했어요 🥲", null));
            }
        });
    }

    public void addComment(Long postId, String content) {
        commentResult.setValue(Resource.loading(null));

        CommentCreateRequest request =
                new CommentCreateRequest(content);

        postRepository.addComment(postId, request, new Callback<>() {
            @Override
            public void onSuccess(CommentResponse response) {
                commentResult.postValue(Resource.success(response));
                loadComments(postId);
            }

            @Override
            public void onError(Exception e) {
                commentResult.postValue(
                        Resource.error("댓글 작성 실패", null)
                );
            }
        });
    }

    public void addReply(
            Long commentId,
            String content,
            Long postId
    ) {
        commentResult.setValue(Resource.loading(null));

        CommentCreateRequest request =
                new CommentCreateRequest(content);

        postRepository.addReply(commentId, request, new Callback<CommentResponse>() {
            @Override
            public void onSuccess(CommentResponse response) {
                commentResult.postValue(Resource.success(response));
                loadComments(postId);
            }

            @Override
            public void onError(Exception e) {
                commentResult.postValue(
                        Resource.error("답글 작성 실패", null)
                );
            }
        });
    }
}