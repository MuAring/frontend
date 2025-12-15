package org.maru.muaring.feature.home.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.maru.muaring.data.api.dto.MyGroupSummary;
import org.maru.muaring.data.repository.GroupRepository;
import org.maru.muaring.feature.R;
import org.maru.muaring.feature.group.ui.CreateGroupActivity;
import org.maru.muaring.feature.home.ui.adapter.GroupSelectorAdapter;
import org.maru.muaring.feature.home.ui.model.HomeViewModel;
import org.maru.muaring.feature.recommend.RecommendMusicFragment;
import org.maru.muaring.feature.today.ui.TodayPostsFragment;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements GroupSelectorAdapter.Listener {

    private static final String TAG = "HomeFragment";

    private static final String TAG_TODAY_ME = "today_me";
    private static final String TAG_TODAY_GROUP_PREFIX = "today_group_";

    private static final String TAG_RECOMMEND = "recommend_music";

    private HomeViewModel viewModel;
    private RecyclerView rvGroupSelector;
    private GroupSelectorAdapter groupSelectorAdapter;
    private ImageView ivHomeIcon;

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

        // 애니메이터 활성화!
        rvGroupSelector.setItemAnimator(new DefaultItemAnimator());

        // 내 프로필 설정 관찰해서 프사 반영
        observeMyProfileSettings();

        // 최근 많이 공유된 음악(지난 7일 기준) - 한 번만 붙이기
        ensureRecommendMusicFragment(savedInstanceState);

        // 기본으로 "나"의 오늘의 음악 보여주기
        showTodayPostsFragment(null);

        View header = view.findViewById(R.id.include_home_header);
        ivHomeIcon = header.findViewById(org.maru.muaring.design.R.id.ivHomeIcon);
        ivHomeIcon.setOnClickListener(v -> {
            NavHostFragment
                    .findNavController(this)
                    .navigate(R.id.mapFragment);
        });
    }

    private void observeMyProfileSettings() {
        viewModel.getMySettings().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            switch (resource.status) {
                case SUCCESS:
                    if (resource.data != null) {
                        groupSelectorAdapter.setMyProfileImage(resource.data.getImageUrl());
                    }
                    break;
                case ERROR:
                    groupSelectorAdapter.setMyProfileImage(null);
                    break;
                case LOADING:
                default:
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
        if (groupSelectorAdapter.getGroupCount() > 0) {
            groupSelectorAdapter.setExpanded(!groupSelectorAdapter.isExpanded());
            return;
        }

        // 아직 한 번도 안 받아온 경우에만 API 호출
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
                default:
                    break;
            }
        });

    }

    private void ensureRecommendMusicFragment(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) return; // 회전/복원 시 중복 add 방지

        FragmentManager fm = getChildFragmentManager();
        Fragment existing = fm.findFragmentByTag(TAG_RECOMMEND);

        if (existing == null) {
            fm.beginTransaction()
                    .add(R.id.container_recommend_music, new RecommendMusicFragment(), TAG_RECOMMEND)
                    .commit();
        }
    }


    /**
     * replace()로 새로 만드는 게 아니라,
     * - 이미 있으면 show()
     * - 없으면 add()
     * - 나머지는 hide()
     */
    private void showTodayPostsFragment(@Nullable Long groupId) {
        FragmentManager fm = getChildFragmentManager();

        String targetTag = (groupId == null)
                ? TAG_TODAY_ME
                : (TAG_TODAY_GROUP_PREFIX + groupId);

        FragmentTransactionHelper txHelper = new FragmentTransactionHelper(fm);

        Fragment target = fm.findFragmentByTag(targetTag);
        if (target == null) {
            // groupId에 따라 다른 팩토리로 생성
            if (groupId == null) {
                target = TodayPostsFragment.newInstanceForHome();
            } else {
                target = TodayPostsFragment.newInstanceForGroup(groupId);
            }
            txHelper.add(R.id.container_today_posts, target, targetTag);
        }

        // 컨테이너 안의 TodayPostsFragment들 전부 hide 후 target만 show
        List<Fragment> fragments = fm.getFragments();
        for (Fragment f : fragments) {
            if (f == null) continue;
            if (f == target) continue;

            if (f instanceof TodayPostsFragment) {
                txHelper.hide(f);
            }
        }

        txHelper.show(target);

        txHelper.commit();

//        TodayPostsFragment fragment = TodayPostsFragment.newInstance(groupId);
//        getChildFragmentManager()
//                .beginTransaction()
//                .replace(R.id.container_today_posts, fragment)
//                .commit();

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
        // 플래그 대신 항상 이 메서드만 호출하도록 변경
        loadGroupsIfNeeded();
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

    // FragmentTransaction 체이닝을 깔끔하게 하려고 만든 helper
    private static class FragmentTransactionHelper {
        private final androidx.fragment.app.FragmentTransaction tx;

        FragmentTransactionHelper(FragmentManager fm) {
            tx = fm.beginTransaction();
        }

        void add(int containerId, Fragment f, String tag) {
            tx.add(containerId, f, tag);
        }

        void hide(Fragment f) {
            tx.hide(f);
        }

        void show(Fragment f) {
            tx.show(f);
        }

        void commit() {
            tx.commit();
        }
    }
}
