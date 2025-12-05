package org.maru.muaring.feature.home.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.maru.muaring.data.api.dto.MyGroupSummary;
import org.maru.muaring.data.repository.GroupRepository;
import org.maru.muaring.feature.R;
import org.maru.muaring.feature.group.ui.CreateGroupActivity;
import org.maru.muaring.feature.home.ui.adapter.GroupSelectorAdapter;
import org.maru.muaring.feature.home.ui.model.HomeViewModel;
import org.maru.muaring.feature.today.ui.TodayPostsFragment;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements GroupSelectorAdapter.Listener {

    private HomeViewModel viewModel;
    private RecyclerView rvGroupSelector;
    private GroupSelectorAdapter groupSelectorAdapter;

    @Inject
    GroupRepository groupRepository;

    private boolean groupsLoaded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new androidx.lifecycle.ViewModelProvider(this)
                .get(HomeViewModel.class);
        rvGroupSelector = view.findViewById(R.id.rvGroupSelector);
        setupGroupSelector();

        // 내 프로필 설정 관찰해서 프사 반영
        observeMyProfileSettings();

        // 기본으로 "나"의 오늘의 음악 보여주기
        showTodayPostsFragment(null);
    }

    private void observeMyProfileSettings() {
        viewModel.getMySettings().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            switch (resource.status) {
                case LOADING:
                    // 로딩 중엔 기본 아이콘(ic_profile_me) 그대로 두면 됨
                    break;

                case SUCCESS:
                    if (resource.data != null) {
                        groupSelectorAdapter.setMyProfileImage(resource.data.getImageUrl());
                    }
                    break;

                case ERROR:
                    // 에러 시에는 그냥 기본 이미지 쓰도록 null로 초기화
                    groupSelectorAdapter.setMyProfileImage(null);
                    break;
            }
        });
    }


    private void setupGroupSelector() {
        groupSelectorAdapter = new GroupSelectorAdapter(this);
        LinearLayoutManager lm =
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvGroupSelector.setLayoutManager(lm);
        rvGroupSelector.setAdapter(groupSelectorAdapter);
    }

    private void loadGroupsIfNeeded() {
        if (groupsLoaded) {
            groupSelectorAdapter.setExpanded(!groupSelectorAdapter.isExpanded());
            return;
        }

        groupRepository.getMyGroups().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            switch (resource.status) {

                case SUCCESS:
                    groupsLoaded = true;
                    List<MyGroupSummary> groups = resource.data;
                    groupSelectorAdapter.setGroups(groups);
                    groupSelectorAdapter.setExpanded(true);
                    break;

                case ERROR:
                    Toast.makeText(
                            getContext(),
                            resource.message,
                            Toast.LENGTH_SHORT
                    ).show();
                    break;

                case LOADING:
                    // TODO: 로딩 처리
                    break;
            }
        });

    }

    private void showTodayPostsFragment(@Nullable Long groupId) {
//        Toast.makeText(
//                getContext(),
//                "오늘의 음악을 보여 드릴게요!",
//                Toast.LENGTH_SHORT
//        ).show();

        TodayPostsFragment fragment = TodayPostsFragment.newInstance(groupId);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.container_today_posts, fragment)
                .commit();
    }

    // ===== GroupSelectorAdapter.Listener =====

    @Override
    public void onMeClicked() {
        // "나" 선택 상태
        groupSelectorAdapter.setSelectedGroup(null);
        // "나" 기준 오늘의 음악
        showTodayPostsFragment(null);
    }

    @Override
    public void onGroupToggleClicked() {
        Log.d("HomeFragment", "onGroupToggleClicked called");
        if (!groupsLoaded) {
            loadGroupsIfNeeded();
        } else {
            groupSelectorAdapter.setExpanded(!groupSelectorAdapter.isExpanded());
        }
    }

    @Override
    public void onGroupItemClicked(MyGroupSummary group) {
        groupSelectorAdapter.setSelectedGroup(group.getGroupId());
        // 해당 그룹 기준 오늘의 음악
        showTodayPostsFragment(group.getGroupId());
    }

    @Override
    public void onAddGroupClicked() {
        Intent intent = new Intent(requireContext(), CreateGroupActivity.class);
        startActivity(intent);
    }
}
