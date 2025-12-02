package org.maru.muaring.feature.home.ui;

import android.content.Intent;
import android.os.Bundle;
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
import org.maru.muaring.feature.today.ui.TodayPostsFragment;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements GroupSelectorAdapter.Listener {

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

        rvGroupSelector = view.findViewById(R.id.rvGroupSelector);
        setupGroupSelector();

        // 기본으로 "나"의 오늘의 음악 보여주고 싶으면 여기에서:
        showTodayPostsFragment(null);
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
        TodayPostsFragment fragment = TodayPostsFragment.newInstance(groupId);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.container_today_posts, fragment)
                .commit();
    }

    // ===== GroupSelectorAdapter.Listener =====

    @Override
    public void onMeClicked() {
        // "나" 기준 오늘의 음악
        showTodayPostsFragment(null);
    }

    @Override
    public void onGroupToggleClicked() {
        if (!groupsLoaded) {
            loadGroupsIfNeeded();
        } else {
            groupSelectorAdapter.setExpanded(!groupSelectorAdapter.isExpanded());
        }
    }

    @Override
    public void onGroupItemClicked(MyGroupSummary group) {
        Toast.makeText(
                getContext(),
                "그룹 클릭: " + group.getName() + " (id=" + group.getGroupId() + ")",
                Toast.LENGTH_SHORT
        ).show();

        // 해당 그룹 기준 오늘의 음악
        showTodayPostsFragment(group.getGroupId());
    }

    @Override
    public void onAddGroupClicked() {
        // TODO: 그룹 생성 화면으로 이동
        Intent intent = new Intent(requireContext(), CreateGroupActivity.class);
        startActivity(intent);
    }
}
