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

    private final MediatorLiveData<Resource<String>> inviteLinkForSharing = new MediatorLiveData<>();

    public LiveData<Resource<String>> getInviteLinkForSharing() {
        return inviteLinkForSharing;
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
                    inviteLinkStatus.removeSource(source);
                    break;

                case ERROR:
                    Log.e(TAG, "초대 링크 생성 실패: " + resource.message);
                    inviteLinkStatus.setValue(Resource.error(resource.message, null));
                    inviteLinkStatus.removeSource(source);
                    break;
            }
        });
    }

    // 친구 초대 하기 기능
    public void createInviteLinkForSharing(Long groupId) {
        Log.d(TAG, "공유용 초대 링크 생성 요청 - groupId: " + groupId);

        LiveData<Resource<GroupInviteResponse>> source = groupRepository.createInviteLink(groupId);

        inviteLinkForSharing.addSource(source, resource -> {
            switch (resource.status) {
                case LOADING:
                    inviteLinkForSharing.setValue(Resource.loading(null));
                    break;

                case SUCCESS:
                    if (resource.data != null) {
                        String inviteUrl = resource.data.getInviteUrl();
                        inviteLinkForSharing.setValue(Resource.success(inviteUrl));
                    } else {
                        inviteLinkForSharing.setValue(Resource.error("초대 링크를 생성할 수 없습니다.", null));
                    }
                    inviteLinkForSharing.removeSource(source);
                    break;

                case ERROR:
                    inviteLinkForSharing.setValue(Resource.error(resource.message, null));
                    inviteLinkForSharing.removeSource(source);
                    break;
            }
        });
    }
}