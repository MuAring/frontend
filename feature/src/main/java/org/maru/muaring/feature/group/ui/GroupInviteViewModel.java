package org.maru.muaring.feature.group.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.dto.GroupInviteResponse;
import org.maru.muaring.data.repository.GroupRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class GroupInviteViewModel extends ViewModel {

    private final GroupRepository groupRepository;
    private final MutableLiveData<Long> createInviteTrigger = new MutableLiveData<>();

    private final LiveData<Resource<GroupInviteResponse>> inviteLinkResult;

    // 생성 후 수행할 액션 (복사 or 공유)
    public enum InviteAction {
        COPY,
        SHARE,
        NONE
    }

    private final MutableLiveData<InviteAction> pendingAction = new MutableLiveData<>(InviteAction.NONE);

    @Inject
    public GroupInviteViewModel(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;

        // groupId가 설정될 때마다 자동으로 API 호출
        inviteLinkResult = Transformations.switchMap(createInviteTrigger,
                groupId -> groupRepository.createInviteLink(groupId));
    }

    public LiveData<Resource<GroupInviteResponse>> getInviteLinkResult() {
        return inviteLinkResult;
    }

    public LiveData<InviteAction> getPendingAction() {
        return pendingAction;
    }

    public void createInviteLinkForCopy(Long groupId) {
        pendingAction.setValue(InviteAction.COPY);
        createInviteTrigger.setValue(groupId);
    }

    public void createInviteLinkForShare(Long groupId) {
        pendingAction.setValue(InviteAction.SHARE);
        createInviteTrigger.setValue(groupId);
    }

    public void clearPendingAction() {
        pendingAction.setValue(InviteAction.NONE);
    }
}