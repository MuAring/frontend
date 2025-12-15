package org.maru.muaring.feature.group.ui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.dto.MusicArchiveDto;
import org.maru.muaring.data.repository.GroupRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class GroupArchiveViewModel extends ViewModel {

    private static final String TAG = "GroupArchiveViewModel";
    private final GroupRepository groupRepository;

    private final MediatorLiveData<Resource<List<MusicArchiveDto>>> musicArchive = new MediatorLiveData<>();
    private final MutableLiveData<Resource<List<MusicArchiveDto>>> loadMoreState = new MutableLiveData<>();

    private Long currentGroupId;
    private int currentPage = 0;
    private boolean isLoading = false;
    private boolean hasMorePages = true;
    private final List<MusicArchiveDto> allMusicList = new ArrayList<>();

    @Inject
    public GroupArchiveViewModel(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public LiveData<Resource<List<MusicArchiveDto>>> getMusicArchive() {
        return musicArchive;
    }

    public LiveData<Resource<List<MusicArchiveDto>>> getLoadMoreState() {
        return loadMoreState;
    }

    public void loadMusicArchive(Long groupId) {
        if (currentGroupId != null && currentGroupId.equals(groupId)) {
            return;
        }

        currentGroupId = groupId;
        currentPage = 0;
        hasMorePages = true;
        allMusicList.clear();

        Log.d(TAG, "음악 보관함 로드 시작 - groupId: " + groupId);

        LiveData<Resource<List<MusicArchiveDto>>> source = groupRepository.getGroupMusicArchive(groupId, currentPage);
        musicArchive.addSource(source, resource -> {
            if (resource != null) {
                // LOADING 상태는 그대로 전달
                if (resource.status == Resource.Status.LOADING) {
                    musicArchive.setValue(resource);
                    return;
                }

                isLoading = false;

                if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    Log.d(TAG, "데이터 로드 성공 - 음악 수: " + resource.data.size());
                    allMusicList.addAll(resource.data);
                    hasMorePages = !resource.data.isEmpty() && resource.data.size() >= 20;
                    currentPage++;
                    musicArchive.setValue(Resource.success(new ArrayList<>(allMusicList)));
                } else if (resource.status == Resource.Status.ERROR) {
                    Log.e(TAG, "데이터 로드 실패: " + resource.message);
                    musicArchive.setValue(resource);
                }

                musicArchive.removeSource(source);
            }
        });

        isLoading = true;
    }

    public void loadNextPage() {
        if (isLoading || !hasMorePages || currentGroupId == null) {
            return;
        }

        Log.d(TAG, "다음 페이지 로드 - page: " + currentPage);

        isLoading = true;
        LiveData<Resource<List<MusicArchiveDto>>> source = groupRepository.getGroupMusicArchive(currentGroupId, currentPage);

        MediatorLiveData<Resource<List<MusicArchiveDto>>> mediator = new MediatorLiveData<>();
        mediator.addSource(source, resource -> {
            if (resource != null) {
                if (resource.status == Resource.Status.LOADING) {
                    loadMoreState.setValue(resource);
                    return;
                }

                isLoading = false;

                if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    Log.d(TAG, "추가 페이지 로드 성공 - 음악 수: " + resource.data.size());
                    allMusicList.addAll(resource.data);
                    hasMorePages = resource.data.size() >= 20;
                    currentPage++;
                    loadMoreState.setValue(Resource.success(resource.data));
                    // 전체 리스트도 업데이트
                    musicArchive.setValue(Resource.success(new ArrayList<>(allMusicList)));
                } else if (resource.status == Resource.Status.ERROR) {
                    Log.e(TAG, "추가 페이지 로드 실패: " + resource.message);
                    loadMoreState.setValue(resource);
                }

                mediator.removeSource(source);
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        allMusicList.clear();
    }
}