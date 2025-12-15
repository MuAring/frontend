package org.maru.muaring.feature.recommend;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.dto.DailyTopMusicResponse;
import org.maru.muaring.data.repository.StatsRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RecommendMusicViewModel extends ViewModel {

    private final StatsRepository statsRepository;

    private final MediatorLiveData<Resource<DailyTopMusicResponse>> _top3Last7Days =
            new MediatorLiveData<>();
    public LiveData<Resource<DailyTopMusicResponse>> top3Last7Days = _top3Last7Days;

    @Inject
    public RecommendMusicViewModel(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    public void loadTop3Last7Days() {
        _top3Last7Days.addSource(statsRepository.getLast7DaysTop3(), res -> {
            _top3Last7Days.setValue(res);
        });
    }
}
