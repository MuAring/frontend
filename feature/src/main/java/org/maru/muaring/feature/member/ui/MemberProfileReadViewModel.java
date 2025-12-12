package org.maru.muaring.feature.member.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.dto.MemberProfileReadResponse;
import org.maru.muaring.data.api.dto.MusicHistoryResponse;
import org.maru.muaring.data.repository.HistoryRepository;
import org.maru.muaring.data.repository.MemberRepository;
import org.maru.muaring.feature.history.ui.model.HistoryMapper;
import org.maru.muaring.feature.history.ui.model.MusicHistoryItem;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import dagger.hilt.android.lifecycle.HiltViewModel;
import javax.inject.Inject;

@HiltViewModel
public class MemberProfileReadViewModel extends ViewModel {

    private final MemberRepository memberRepository;
    private final HistoryRepository historyRepository;
    private final MediatorLiveData<Resource<MemberProfileReadResponse>> memberProfile =
            new MediatorLiveData<>();

    private final MediatorLiveData<Resource<List<MusicHistoryItem>>> memberHistory =
            new MediatorLiveData<>();
    @Inject
    public MemberProfileReadViewModel(MemberRepository memberRepository,
                                      HistoryRepository historyRepository) {
        this.memberRepository = memberRepository;
        this.historyRepository = historyRepository;

        memberProfile.setValue(Resource.loading(null));
        memberHistory.setValue(Resource.loading(Collections.emptyList()));
    }

    // 프로필
    public LiveData<Resource<MemberProfileReadResponse>> getMemberProfile() {
        return memberProfile;
    }

    public void loadMemberProfile(Long memberId) {
        LiveData<Resource<MemberProfileReadResponse>> source =
                memberRepository.getMemberProfile(memberId);

        memberProfile.addSource(source, res -> {
            if (res == null) return;

            switch (res.status) {
                case LOADING:
                    memberProfile.setValue(Resource.loading(null));
                    break;

                case SUCCESS:
                    memberProfile.setValue(Resource.success(res.data));
                    memberProfile.removeSource(source);
                    break;

                case ERROR:
                    memberProfile.setValue(Resource.error(res.message, null));
                    memberProfile.removeSource(source);
                    break;
            }
        });
    }

    // 히스토리
    public LiveData<Resource<List<MusicHistoryItem>>> getMemberHistory() {
        return memberHistory;
    }

    public void loadMemberHistory(Long memberId,
                                  @Nullable Integer year,
                                  @Nullable Integer month,
                                  int page) {

        LiveData<Resource<List<MusicHistoryResponse>>> source =
                historyRepository.getMemberHistory(memberId, year, month, page);

        memberHistory.addSource(source, res -> {
            if (res == null) return;

            switch (res.status) {

                case LOADING:
                    memberHistory.setValue(Resource.loading(null));
                    break;

                case SUCCESS:
                    List<MusicHistoryItem> mapped =
                            HistoryMapper.toItemList(res.data);
                    memberHistory.setValue(Resource.success(mapped));
                    memberHistory.removeSource(source);
                    break;

                case ERROR:
                    memberHistory.setValue(Resource.error(res.message, null));
                    memberHistory.removeSource(source);
                    break;
            }
        });
    }
}
