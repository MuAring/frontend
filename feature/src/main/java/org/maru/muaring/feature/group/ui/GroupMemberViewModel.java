package org.maru.muaring.feature.group.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.maru.muaring.data.api.dto.GroupMemberResponse;
import org.maru.muaring.data.repository.GroupRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class GroupMemberViewModel extends ViewModel {

    private final GroupRepository groupRepository;

    private final MutableLiveData<List<GroupMemberResponse>> _members = new MutableLiveData<>(new ArrayList<>());
    public final LiveData<List<GroupMemberResponse>> members = _members;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public final LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public final LiveData<String> errorMessage = _errorMessage;

    @Inject
    public GroupMemberViewModel(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public void loadGroupMembers(Long groupId, String search) {
        _isLoading.setValue(true);

        groupRepository.getGroupMembers(groupId, search, new GroupRepository.GetGroupMembersCallback() {
            @Override
            public void onSuccess(List<GroupMemberResponse> memberList) {
                _isLoading.setValue(false);
                _members.setValue(memberList);
            }

            @Override
            public void onError(Throwable t) {
                _isLoading.setValue(false);
                _errorMessage.setValue(t.getMessage());
            }
        });
    }
}