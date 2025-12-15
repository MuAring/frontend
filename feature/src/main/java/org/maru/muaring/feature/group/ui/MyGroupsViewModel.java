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
    private Long currentMemberId; // 현재 조회 중인 멤버 ID

    @Inject
    public MyGroupsViewModel(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public LiveData<Resource<List<MyGroupSummary>>> getGroups() {
        return groups;
    }

    /**
     * 특정 멤버의 그룹 목록 로드
     * @param memberId 조회할 멤버 ID (null이면 본인 조회)
     * @param searchName 검색어 (null이면 전체 조회)
     */
    public void loadMemberGroups(Long memberId, String searchName) {
        currentMemberId = memberId;

        LiveData<Resource<List<MyGroupSummary>>> source =
                groupRepository.getMemberGroupsWithSearch(memberId, searchName);

        groups.addSource(source, groups::setValue);
    }

    /**
     * 그룹 검색
     * @param memberId 조회할 멤버 ID
     * @param query 검색어
     */
    public void searchGroups(Long memberId, String query) {
        searchQuery.setValue(query);
        loadMemberGroups(memberId, query);
    }

    /**
     * 현재 멤버의 그룹 목록 새로고침
     */
    public void refreshGroups() {
        String currentQuery = searchQuery.getValue();
        loadMemberGroups(currentMemberId, currentQuery);
    }
}