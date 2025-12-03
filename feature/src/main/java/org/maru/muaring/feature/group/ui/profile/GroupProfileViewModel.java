package org.maru.muaring.feature.group.ui.profile;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.dto.MusicHistoryResponse;
import org.maru.muaring.data.repository.HistoryRepository;
import org.maru.muaring.feature.history.ui.model.HistoryMapper;
import org.maru.muaring.feature.history.ui.model.MusicHistoryItem;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

@dagger.hilt.android.lifecycle.HiltViewModel
public class GroupProfileViewModel extends ViewModel {

    private final HistoryRepository historyRepository;

    private final MediatorLiveData<Resource<List<MusicHistoryItem>>> groupHistory =
            new MediatorLiveData<>();

    @Inject
    public GroupProfileViewModel(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
        groupHistory.setValue(Resource.loading(Collections.emptyList()));
    }

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