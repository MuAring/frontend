package org.maru.muaring.feature.search.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.maru.muaring.core.ui.SegmentedToggleView;
import org.maru.muaring.data.api.dto.GroupSummary;
import org.maru.muaring.data.repository.GroupRepository;
import org.maru.muaring.feature.R;
import org.maru.muaring.feature.common.SearchBarFragment;
import org.maru.muaring.feature.search.ui.adapter.SearchResultAdapter;
import org.maru.muaring.feature.search.ui.model.SearchResultItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SearchFragment extends Fragment {

    private SegmentedToggleView segmentedToggleView;    // 상단 토글
    private ImageButton btnBack;                        // 뒤로가기 버튼
    private TextView textTitle;
    private RecyclerView recyclerSearchResult;          // 하단 리사이클러뷰

    private SearchBarFragment searchBarFragment;        // 검색창
    private SearchResultAdapter searchResultAdapter;

    private SearchNavigator navigator;

    @Inject
    GroupRepository groupRepository;

    // Activity를 navigator로 받기
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof SearchNavigator) {
            navigator = (SearchNavigator) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupInitialState();
        setupListeners();
        setupRecyclerView();
    }

    private void initViews(View view) {
        segmentedToggleView = view.findViewById(R.id.segmentedToggle);
        btnBack = view.findViewById(R.id.btn_back);
        textTitle = view.findViewById(R.id.text_title);
        searchBarFragment = (SearchBarFragment) getChildFragmentManager()
                .findFragmentById(R.id.fragmentSearchBar);
        recyclerSearchResult = view.findViewById(R.id.recyclerSearchResult);
    }

    private void setupInitialState() {
        // 초기 상태: "그룹" 선택
        segmentedToggleView.selectGroup();
        updateUIForGroupSearch();
    }


    private void setupRecyclerView() {
        recyclerSearchResult.setLayoutManager(new LinearLayoutManager(requireContext()));

        searchResultAdapter = new SearchResultAdapter(new SearchResultAdapter.OnItemActionClickListener() {
            @Override
            public void onItemClick(SearchResultItem item) {
                // 카드 전체 클릭 → 그룹 프로필 화면으로 이동
                if (item.getType() == SearchResultItem.Type.GROUP) {
                    Long groupId = item.getId();

                    if (navigator != null) {
                        navigator.openGroupProfile(groupId);   // 여기서 Activity에게 부탁
                    }
                } else {
                    // TODO: 사용자 프로필 이동
                }
            }

            @Override
            public void onActionClick(SearchResultItem item) {
                // 버튼 클릭 → 가입 처리
                if (item.getType() == SearchResultItem.Type.GROUP) {

                    // 이미 참여 중이면 클릭 막기
                    if (Boolean.TRUE.equals(item.getIsJoined())) return;

                    Long groupId = item.getId();

                    groupRepository.joinPublicGroup(groupId, new GroupRepository.JoinGroupCallback() {
                        @Override
                        public void onSuccess() {
                            if (!isAdded()) return;

                            Toast.makeText(requireContext(), "가입을 완료했어요! 🎵", Toast.LENGTH_SHORT).show();

                            // 가입 상태 변경
                            item.setIsJoined(true);
                            item.setActionText("가입 중");

                            // 어댑터에 반영
                            searchResultAdapter.refreshItem(item);
                        }

                        @Override
                        public void onError(Throwable t) {
                            if (!isAdded()) return;

                            Toast.makeText(requireContext(), "가입에 실패했어요. 🥲", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // TODO: 사용자 팔로우
                }
            }
        });
        recyclerSearchResult.setAdapter(searchResultAdapter);
    }

    private void setupListeners() {
        // 뒤로가기
        btnBack.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(SearchFragment.this);
            navController.popBackStack();
        });

        // 토글 리스너
        segmentedToggleView.setOnSegmentSelectedListener(new SegmentedToggleView.OnSegmentSelectedListener() {
            @Override
            public void onGroupSelected() {
                updateUIForGroupSearch();
                clearSearchResults();
            }

            @Override
            public void onUserSelected() {
                updateUIForUserSearch();
                clearSearchResults();
            }
        });

        // 검색바 콜백
        if (searchBarFragment != null) {
            searchBarFragment.setOnSearchClickListener(query -> {
                performSearch(query);
            });
        }
    }

    private void updateUIForGroupSearch() {
        textTitle.setText("어떤 그룹을 찾아볼까요?");
        if (searchBarFragment != null) {
//            searchBarFragment.setHint("그룹 이름 검색");
            searchBarFragment.clearQuery();
        }
    }

    private void updateUIForUserSearch() {
        textTitle.setText("어떤 사용자를 찾아볼까요?");
        if (searchBarFragment != null) {
//            searchBarFragment.setHint("사용자 닉네임 검색");
            searchBarFragment.clearQuery();
        }
    }

    private void clearSearchResults() {
        if (searchResultAdapter != null) {
            searchResultAdapter.clear();
        }
    }

    private void performSearch(String query) {
        if (query.trim().isEmpty()) {
            clearSearchResults();
            return;
        }

        if (segmentedToggleView.isGroupSelected()) {
            searchGroups(query);
        } else {
            searchUsers(query);
        }
    }

    private void searchGroups(String query) {
        groupRepository.searchGroups(query, 0, 10,
                new GroupRepository.SearchGroupsCallback() {
                    @Override
                    public void onSuccess(List<GroupSummary> groups) {
                        if (!isAdded()) return;

                        List<SearchResultItem> items = new ArrayList<>();
                        for (GroupSummary g : groups) {
                            List<String> categoryNames = g.getCategoryNames();

                            boolean joined = Boolean.TRUE.equals(g.getIsJoined());
                            String actionText = joined ? "가입 중" : "가입";

                            items.add(new SearchResultItem(
                                    SearchResultItem.Type.GROUP,
                                    g.getGroupId(),
                                    g.getName(),
                                    categoryNames,
                                    null,
                                    actionText,
                                    joined
                            ));
                        }
                        searchResultAdapter.setItems(items);
                    }

                    @Override
                    public void onError(Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(requireContext(), "그룹 검색 실패", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void searchUsers(String query) {
        // TODO: 사용자 검색 API 연동 후 SearchResultItem.Type.USER 로 매핑

//        items.add(new SearchResultItem(
//                SearchResultItem.Type.USER,
//                u.getUserId(),
//                u.getNickname(),
//                null,
//                todayMusic,
//                "팔로우",
//                null   // isJoined 안 씀
//        ));

        // 지금은 빈 처리
        Toast.makeText(requireContext(), "사용자 검색 API 연결 예정", Toast.LENGTH_SHORT).show();
    }

}