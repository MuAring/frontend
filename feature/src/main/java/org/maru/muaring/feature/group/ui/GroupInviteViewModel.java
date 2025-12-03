package org.maru.muaring.feature.group.ui;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.dto.GroupInviteResponse;
import org.maru.muaring.data.repository.GroupRepository;
import javax.inject.Inject;

@HiltViewModel
public class GroupInviteViewModel extends ViewModel {

    private static final String TAG = "GroupInviteViewModel";

    private final GroupRepository groupRepository;
    private final MediatorLiveData<Resource<String>> inviteLinkStatus = new MediatorLiveData<>();

    @Inject
    public GroupInviteViewModel(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public LiveData<Resource<String>> getInviteLinkStatus() {
        return inviteLinkStatus;
    }

    public void createInviteLink(Long groupId) {
        Log.d(TAG, "초대 링크 생성 요청 - groupId: " + groupId);

        LiveData<Resource<GroupInviteResponse>> source = groupRepository.createInviteLink(groupId);

        inviteLinkStatus.addSource(source, resource -> {
            switch (resource.status) {
                case LOADING:
                    inviteLinkStatus.setValue(Resource.loading(null));
                    break;

                case SUCCESS:
                    if (resource.data != null) {
                        String inviteUrl = resource.data.getInviteUrl();
                        Log.d(TAG, "초대 링크 생성 성공: " + inviteUrl);
                        inviteLinkStatus.setValue(Resource.success(inviteUrl));
                    } else {
                        inviteLinkStatus.setValue(Resource.error("초대 링크를 생성할 수 없습니다.", null));
                    }
                    break;

                case ERROR:
                    Log.e(TAG, "초대 링크 생성 실패: " + resource.message);
                    inviteLinkStatus.setValue(Resource.error(resource.message, null));
                    break;
            }

            // source 제거 (메모리 누수 방지)
            inviteLinkStatus.removeSource(source);
        });
    }
}