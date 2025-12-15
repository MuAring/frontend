package org.maru.muaring.feature.today.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.dto.LibraryMusicDTO;
import org.maru.muaring.data.api.dto.LikeResponseDTO;
import org.maru.muaring.data.api.dto.MusicPostFeedResponse;
import org.maru.muaring.data.repository.LibraryRepository;
import org.maru.muaring.data.repository.LikeRepository;
import org.maru.muaring.data.repository.PostRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TodayPostsViewModel extends ViewModel {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final LibraryRepository libraryRepository;

    private final MediatorLiveData<Resource<List<MusicPostFeedResponse>>> _posts =
            new MediatorLiveData<>();
    public LiveData<Resource<List<MusicPostFeedResponse>>> posts = _posts;

    // 화면 상태 유지용
    private List<MusicPostFeedResponse> currentList = new ArrayList<>();

    @Inject
    public TodayPostsViewModel(
            PostRepository postRepository,
            LikeRepository likeRepository,
            LibraryRepository libraryRepository
    ) {
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.libraryRepository = libraryRepository;
    }

    // =====================================================
    // 공통 로딩 로직
    // =====================================================
    private void loadPosts(LiveData<Resource<List<MusicPostFeedResponse>>> source) {
        _posts.setValue(Resource.loading(currentList));

        _posts.addSource(source, res -> {
            if (res == null) return;

            if (res.status == Resource.Status.SUCCESS) {
                currentList = (res.data != null)
                        ? new ArrayList<>(res.data)
                        : new ArrayList<>();
                _posts.setValue(Resource.success(currentList));

            } else if (res.status == Resource.Status.ERROR) {
                _posts.setValue(Resource.error(res.message, currentList));

            } else {
                _posts.setValue(Resource.loading(currentList));
            }

            if (res.status != Resource.Status.LOADING) {
                _posts.removeSource(source);
            }
        });
    }

    // =====================================================
    // 외부에서 부르는 메서드들
    // =====================================================

    // 홈 (followee)
    public void loadForMe() {
        loadPosts(postRepository.getTodayPostsForMe());
    }

    // 그룹 (홈)
    public void loadForGroup(Long groupId) {
        loadPosts(postRepository.getTodayPostsForGroup(groupId));
    }

    // 멤버 프로필 (me)
    public void loadForMeToday() {
        loadPosts(postRepository.getPostsForMeOnly());
    }

    // 그룹 프로필
    public void loadForGroupProfile(Long groupId) {
        loadPosts(postRepository.getGroupPosts(groupId));
    }

    // =====================================================
    // 좋아요 토글
    // =====================================================
    public void toggleLike(Long postId) {
        LiveData<Resource<LikeResponseDTO>> source =
                likeRepository.toggleLike(postId);

        _posts.addSource(source, res -> {
            if (res == null) return;

            if (res.status == Resource.Status.SUCCESS && res.data != null) {
                applyLikeResult(res.data);
            }

            if (res.status != Resource.Status.LOADING) {
                _posts.removeSource(source);
            }
        });
    }

    private void applyLikeResult(LikeResponseDTO dto) {
        List<MusicPostFeedResponse> newList = new ArrayList<>(currentList.size());

        for (MusicPostFeedResponse p : currentList) {
            if (p != null && p.getPostId() != null
                    && p.getPostId().equals(dto.getPostId())) {
                p.setIsLiked(dto.isLiked());
                p.setLikeCount(dto.getNumOfLikes());
            }
            newList.add(p);
        }

        currentList = newList;
        _posts.setValue(Resource.success(currentList));
    }

    // =====================================================
    // 보관함 추가
    // =====================================================
    public void addToLibrary(Long musicId, String category) {
        LiveData<Resource<LibraryMusicDTO>> source =
                libraryRepository.addMusicToLibrary(musicId, category);

        _posts.addSource(source, res -> {
            if (res == null) return;

            if (res.status == Resource.Status.SUCCESS) {
                applyLibraryResult(musicId, true);
            }

            if (res.status != Resource.Status.LOADING) {
                _posts.removeSource(source);
            }
        });
    }

    // =====================================================
    // 보관함 삭제
    // =====================================================
    public void removeFromLibrary(Long musicId) {
        LiveData<Resource<Void>> source =
                libraryRepository.deleteOneMusicFromLibrary(musicId);

        _posts.addSource(source, res -> {
            if (res == null) return;

            if (res.status == Resource.Status.SUCCESS) {
                applyLibraryResult(musicId, false);
            }

            if (res.status != Resource.Status.LOADING) {
                _posts.removeSource(source);
            }
        });
    }

    private void applyLibraryResult(Long musicId, boolean inLibrary) {
        List<MusicPostFeedResponse> newList = new ArrayList<>(currentList.size());

        for (MusicPostFeedResponse p : currentList) {
            if (p != null && p.getMusicId() != null
                    && p.getMusicId().equals(musicId)) {
                p.setInLibrary(inLibrary);
            }
            newList.add(p);
        }

        currentList = newList;
        _posts.setValue(Resource.success(currentList));
    }
}
