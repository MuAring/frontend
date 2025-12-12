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
import java.util.Collections;
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


    // ViewModel이 "현재 화면 상태"를 들고 있어야 덮어쓰기 안 당함
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

    public void loadForGroup(Long groupId) {
        _posts.setValue(Resource.loading(null));
        LiveData<Resource<List<MusicPostFeedResponse>>> source =
                postRepository.getTodayPostsForGroup(groupId);

        _posts.addSource(source, res -> {
            if (res == null) return;

            if (res.status == Resource.Status.SUCCESS) {
                currentList = (res.data != null) ? new ArrayList<>(res.data) : new ArrayList<>();
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

    public void loadForMe() {
        _posts.setValue(Resource.loading(currentList));

        LiveData<Resource<List<MusicPostFeedResponse>>> source =
                postRepository.getTodayPostsForMe();

        _posts.addSource(source, res -> {
            if (res == null) return;

            if (res.status == Resource.Status.SUCCESS) {
                currentList = (res.data != null) ? new ArrayList<>(res.data) : new ArrayList<>();
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

    // =========================
    // 좋아요 토글
    // =========================
    public void toggleLike(Long postId) {
        LiveData<Resource<LikeResponseDTO>> source = likeRepository.toggleLike(postId);
        _posts.addSource(source, res -> {
            if (res == null) return;

            if (res.status == Resource.Status.SUCCESS && res.data != null) {
                applyLikeResult(res.data); // 리스트 내부 반영
            } else if (res.status == Resource.Status.ERROR) {
                // 필요하면 토스트용 메시지 이벤트 따로 만들기
            }

            if (res.status != Resource.Status.LOADING) {
                _posts.removeSource(source);
            }
        });
    }

    private void applyLikeResult(LikeResponseDTO dto) {
        List<MusicPostFeedResponse> newList = new ArrayList<>(currentList.size());

        for (MusicPostFeedResponse p : currentList) {
            if (p != null && p.getPostId() != null && p.getPostId().equals(dto.getPostId())) {
                p.setIsLiked(dto.isLiked());
                p.setLikeCount(dto.getNumOfLikes());
            }
            newList.add(p);
        }

        currentList = newList;
        _posts.setValue(Resource.success(currentList));
    }

//    private void applyLikeResult(LikeResponseDTO dto) {
//        Resource<List<MusicPostFeedResponse>> cur = _posts.getValue();
//        List<MusicPostFeedResponse> oldList = (cur != null && cur.data != null) ? cur.data : Collections.emptyList();
//
//        List<MusicPostFeedResponse> newList = new ArrayList<>(oldList.size());
//        for (MusicPostFeedResponse p : oldList) {
//            if (p != null && p.getPostId() != null && p.getPostId().equals(dto.getPostId())) {
//                // 같은 객체를 수정해도 되지만, submitList 다시 그릴 거라면 이 방식이 깔끔
//                p.setIsLiked(dto.isLiked());
//                p.setLikeCount(dto.getNumOfLikes());
//            }
//            newList.add(p);
//        }
//        _posts.setValue(Resource.success(newList));
//    }

    // =========================
    // 보관함 추가
    // =========================
    public void addToLibrary(Long musicId, String category) {
        LiveData<Resource<LibraryMusicDTO>> source = libraryRepository.addMusicToLibrary(musicId, category);

        _posts.addSource(source, res -> {
            if (res == null) return;

            if (res.status == Resource.Status.SUCCESS) {
                applyLibraryResult(musicId, true);
            } else if (res.status == Resource.Status.ERROR) {
                android.util.Log.e("TodayVM", "addToLibrary ERROR: " + res.message);
            }

            if (res.status != Resource.Status.LOADING) {
                _posts.removeSource(source);
            }
        });
    }

    // =========================
    // 보관함 삭제 (서버는 deleteMultiple이라 list로 던져야 함)
    // =========================
    public void removeFromLibrary(Long musicId) {
        LiveData<Resource<Void>> source =
                libraryRepository.deleteMusicFromLibrary(Collections.singletonList(musicId));

        _posts.addSource(source, res -> {
            if (res == null) return;

            if (res.status == Resource.Status.SUCCESS) {
                applyLibraryResult(musicId, false);
            } else if (res.status == Resource.Status.ERROR) {
                // 실패 시 필요하면 롤백/토스트
            }

            if (res.status != Resource.Status.LOADING) {
                _posts.removeSource(source);
            }
        });
    }


    private void applyLibraryResult(Long musicId, boolean inLibrary) {
        List<MusicPostFeedResponse> newList = new ArrayList<>(currentList.size());

        for (MusicPostFeedResponse p : currentList) {
            if (p != null && p.getMusicId() != null && p.getMusicId().equals(musicId)) {
                p.setInLibrary(inLibrary);
            }
            newList.add(p);
        }

        currentList = newList;
        _posts.setValue(Resource.success(currentList));
    }
}