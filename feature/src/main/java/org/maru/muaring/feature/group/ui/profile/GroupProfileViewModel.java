package org.maru.muaring.feature.group.ui.profile;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.dto.GroupProfileResponse;
import org.maru.muaring.data.api.dto.MusicHistoryResponse;
import org.maru.muaring.data.api.dto.TodayMusicPostResponse;
import org.maru.muaring.data.repository.GroupRepository;
import org.maru.muaring.data.repository.HistoryRepository;
import org.maru.muaring.feature.history.ui.model.HistoryMapper;
import org.maru.muaring.feature.history.ui.model.MusicHistoryItem;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class GroupProfileViewModel extends ViewModel {

    private final GroupRepository groupRepository;
    private final HistoryRepository historyRepository;

    // 그룹 프로필
    private final MediatorLiveData<Resource<GroupProfileResponse>> groupProfile =
            new MediatorLiveData<>();

    // 그룹 히스토리
    private final MediatorLiveData<Resource<List<MusicHistoryItem>>> groupHistory =
            new MediatorLiveData<>();

    // 그룹 오늘 공유한 음악
    private final MediatorLiveData<Resource<TodayMusicPostResponse>> todayMusic =
            new MediatorLiveData<>();

    // 가입 상태 관리
    private final MediatorLiveData<Resource<Void>> joinStatus = new MediatorLiveData<>();

    @Inject
    public GroupProfileViewModel(GroupRepository groupRepository,
                                 HistoryRepository historyRepository) {
        this.groupRepository = groupRepository;
        this.historyRepository = historyRepository;

        groupProfile.setValue(Resource.loading(null));
        groupHistory.setValue(Resource.loading(Collections.emptyList()));
        todayMusic.setValue(Resource.loading(null));
    }

    // ===== 프로필 =====
    public LiveData<Resource<GroupProfileResponse>> getGroupProfile() {
        return groupProfile;
    }

    public void loadGroupProfile(Long groupId) {
        LiveData<Resource<GroupProfileResponse>> source =
                groupRepository.getGroupProfile(groupId);

        groupProfile.addSource(source, res -> {
            if (res == null) return;

            switch (res.status) {
                case LOADING:
                    groupProfile.setValue(Resource.loading(null));
                    break;

                case SUCCESS:
                    groupProfile.setValue(Resource.success(res.data));
                    groupProfile.removeSource(source);
                    break;

                case ERROR:
                    groupProfile.setValue(Resource.error(res.message, null));
                    groupProfile.removeSource(source);
                    break;
            }
        });
    }

    // 그룹 가입 메서드
    public LiveData<Resource<Void>> getJoinStatus() {
        return joinStatus;
    }

    public void joinPublicGroup(Long groupId) {
        joinStatus.setValue(Resource.loading(null));

        groupRepository.joinPublicGroup(groupId, new GroupRepository.JoinGroupCallback() {
            @Override
            public void onSuccess() {
                joinStatus.setValue(Resource.success(null));
                // 가입 성공 후 프로필 다시 로드
                loadGroupProfile(groupId);
            }

            @Override
            public void onError(Throwable t) {
                joinStatus.setValue(Resource.error(t.getMessage(), null));
            }
        });
    }

    // 그룹 오늘 공유한 음악 조회
    public LiveData<Resource<TodayMusicPostResponse>> getTodayMusic() {
        return todayMusic;
    }

    public void loadTodayMusic(Long groupId) {
        LiveData<Resource<TodayMusicPostResponse>> source =
                groupRepository.getTodayGroupFeed(groupId);

        todayMusic.addSource(source, res -> {
            if (res == null) return;

            switch (res.status) {
                case LOADING:
                    todayMusic.setValue(Resource.loading(null));
                    break;

                case SUCCESS:
                    todayMusic.setValue(Resource.success(res.data));
                    todayMusic.removeSource(source);
                    break;

                case ERROR:
                    todayMusic.setValue(Resource.error(res.message, null));
                    todayMusic.removeSource(source);
                    break;
            }
        });
    }

    // ===== 히스토리 =====
    public LiveData<Resource<List<MusicHistoryItem>>> getGroupHistory() {
        return groupHistory;
    }

    /**
     * 그룹 히스토리 조회
     * @param groupId 그룹 ID
     * @param year    null 이면 파라미터 안 보냄
     * @param month   null 이면 파라미터 안 보냄
     * @param page    페이징 (0부터)
     */
    public void loadGroupHistory(Long groupId,
                                 @Nullable Integer year,
                                 @Nullable Integer month,
                                 int page) {

        LiveData<Resource<List<MusicHistoryResponse>>> source =
                historyRepository.getGroupHistory(groupId, year, month, page);

        groupHistory.addSource(source, res -> {
            if (res == null) return;

            switch (res.status) {
                case LOADING:
                    groupHistory.setValue(Resource.loading(null));
                    break;

                case SUCCESS:
                    List<MusicHistoryItem> mapped =
                            HistoryMapper.toItemList(res.data);
                    groupHistory.setValue(Resource.success(mapped));
                    groupHistory.removeSource(source);
                    break;

                case ERROR:
                    groupHistory.setValue(Resource.error(res.message, null));
                    groupHistory.removeSource(source);
                    break;
            }
        });
    }
}
