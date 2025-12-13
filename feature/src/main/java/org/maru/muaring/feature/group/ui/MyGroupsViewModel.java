package org.maru.muaring.feature.group.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.dto.MyGroupSummary;
import org.maru.muaring.data.repository.GroupRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MyGroupsViewModel extends ViewModel {

    private final GroupRepository groupRepository;
    private final MediatorLiveData<Resource<List<MyGroupSummary>>> groups = new MediatorLiveData<>();
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();

    @Inject
    public MyGroupsViewModel(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public LiveData<Resource<List<MyGroupSummary>>> getGroups() {
        return groups;
    }

    public void loadMyGroups(String searchName) {
        // 새로운 검색 메서드 사용
        LiveData<Resource<List<MyGroupSummary>>> source =
                groupRepository.getMyGroupsWithSearch(searchName);

        groups.addSource(source, groups::setValue);
    }

    public void searchGroups(String query) {
        searchQuery.setValue(query);
        loadMyGroups(query);
    }

    public void refreshGroups() {
        String currentQuery = searchQuery.getValue();
        loadMyGroups(currentQuery);
    }
}