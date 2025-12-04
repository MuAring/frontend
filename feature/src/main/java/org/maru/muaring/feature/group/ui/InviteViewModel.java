package org.maru.muaring.feature.group.ui;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.dto.InvitePreviewResponse;
import org.maru.muaring.data.repository.GroupRepository;
import javax.inject.Inject;

@HiltViewModel
public class InviteViewModel extends ViewModel {

    private static final String TAG = "InviteViewModel";

    private final GroupRepository groupRepository;
    private final MediatorLiveData<Resource<InvitePreviewData>> invitePreview = new MediatorLiveData<>();
    private final MediatorLiveData<Resource<Boolean>> joinResult = new MediatorLiveData<>();

    @Inject
    public InviteViewModel(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public LiveData<Resource<InvitePreviewData>> getInvitePreview(String token) {
        Log.d(TAG, "초대 정보 조회 - token: " + token);

        LiveData<Resource<InvitePreviewResponse>> source = groupRepository.getInvitePreview(token);

        invitePreview.addSource(source, resource -> {
            switch (resource.status) {
                case LOADING:
                    invitePreview.setValue(Resource.loading(null));
                    break;

                case SUCCESS:
                    if (resource.data != null) {
                        InvitePreviewData data = InvitePreviewData.from(resource.data);
                        invitePreview.setValue(Resource.success(data));
                    } else {
                        invitePreview.setValue(Resource.error("초대 정보를 찾을 수 없습니다.", null));
                    }
                    invitePreview.removeSource(source);
                    break;

                case ERROR:
                    invitePreview.setValue(Resource.error(resource.message, null));
                    invitePreview.removeSource(source);
                    break;
            }
        });

        return invitePreview;
    }

    public LiveData<Resource<Boolean>> joinGroup(String token) {
        Log.d(TAG, "그룹 참여 요청 - token: " + token);

        LiveData<Resource<Void>> source = groupRepository.joinByInviteToken(token);

        joinResult.addSource(source, resource -> {
            switch (resource.status) {
                case LOADING:
                    joinResult.setValue(Resource.loading(null));
                    break;

                case SUCCESS:
                    joinResult.setValue(Resource.success(true));
                    joinResult.removeSource(source);
                    break;

                case ERROR:
                    joinResult.setValue(Resource.error(resource.message, false));
                    joinResult.removeSource(source);
                    break;
            }
        });

        return joinResult;
    }
}