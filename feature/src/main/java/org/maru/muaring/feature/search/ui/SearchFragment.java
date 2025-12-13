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

import org.maru.muaring.core.common.Callback;
import org.maru.muaring.core.ui.SegmentedToggleView;
import org.maru.muaring.data.api.dto.GroupRecommendListResponseDto;
import org.maru.muaring.data.api.dto.GroupSummary;
import org.maru.muaring.data.api.dto.MemberRecommendItemDto;
import org.maru.muaring.data.api.dto.MemberSearchItemDto;
import org.maru.muaring.data.repository.FollowRepository;
import org.maru.muaring.data.repository.GroupRepository;
import org.maru.muaring.data.repository.MemberRepository;
import org.maru.muaring.data.repository.RecommendationRepository;
import org.maru.muaring.feature.R;
import org.maru.muaring.feature.common.SearchBarFragment;
import org.maru.muaring.feature.search.ui.adapter.SearchResultAdapter;
import org.maru.muaring.feature.search.ui.model.SearchResultItem;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SearchFragment extends Fragment {

    // Toolbar
    private TextView toolbarTitle;
    private ImageButton toolbarBack;
//    private ImageButton toolbarAction;

    private SegmentedToggleView segmentedToggleView;    // 상단 토글
    private ImageButton btnBack;                        // 뒤로가기 버튼
    private TextView textTitle;
    private RecyclerView recyclerSearchResult;          // 하단 리사이클러뷰

    private SearchBarFragment searchBarFragment;        // 검색창
    private SearchResultAdapter searchResultAdapter;
    private TextView tvEmptyResult;                     // 빈 결과 조회
    private TextView tvRecommendTitle;
    private boolean searchHasFocus = false;


    private SearchNavigator navigator;

    @Inject
    GroupRepository groupRepository;

    @Inject
    MemberRepository memberRepository;

    @Inject
    FollowRepository followRepository;

    @Inject
    RecommendationRepository recommendationRepository;

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

        initToolbar(view);
        initViews(view);
        setupRecyclerView();
        setupInitialState();
        setupListeners();
    }

    private void initToolbar(@NonNull View root) {
        View toolbar = root.findViewById(R.id.include_toolbar_group);
        if (toolbar == null) return;

        toolbarTitle = toolbar.findViewById(org.maru.muaring.core.R.id.toolbar_title);
        toolbarBack = toolbar.findViewById(R.id.btn_back);
//        toolbarAction = toolbar.findViewById(org.maru.muaring.core.R.id.toolbar_action);

        toolbarTitle.setText("검색");
        toolbarBack.setVisibility(View.INVISIBLE);
//        toolbarBack.setOnClickListener(v -> requireActivity().onBackPressed());
//        toolbarAction.setVisibility(View.VISIBLE);
//        toolbarAction.setOnClickListener(v -> {
//            // TODO: 그룹 설정 이동
//        });
    }

    private void initViews(View view) {
        segmentedToggleView = view.findViewById(R.id.segmentedToggle);
        btnBack = view.findViewById(R.id.btn_back);
        textTitle = view.findViewById(R.id.text_title);
        searchBarFragment = (SearchBarFragment) getChildFragmentManager()
                .findFragmentById(R.id.fragmentSearchBar);
        recyclerSearchResult = view.findViewById(R.id.recyclerSearchResult);
        tvEmptyResult = view.findViewById(R.id.tvEmptyResult);
        tvRecommendTitle = view.findViewById(R.id.tvRecommendTitle);
    }

    private void setupInitialState() {
        // 초기 상태: "그룹" 선택
        segmentedToggleView.selectGroup();
        updateUIForGroupSearch();

        // 검색 비활성 + 검색어 없음 → 추천 띄우기
        loadRecommendationsByToggle();
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
                        // 추천 클릭 로그
//                        recommendationRepository.logGroupClick(groupId);
                    }
                } else {
                    // TODO: 사용자 프로필 이동
                    Long memberId = item.getId();

                    // 멤버 추천 / 멤버 검색 → 멤버 프로필
                    if (navigator != null) {
                        navigator.openMemberProfile(memberId);
                        // 추천 클릭 로그
//                      recommendationRepository.logMemberClick(memberId);
                    }
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

                            Toast.makeText(requireContext(), item.getTitle() + " 가입을 완료했어요! 🎵", Toast.LENGTH_SHORT).show();

                            // 가입 상태 변경
                            item.setIsJoined(true);
                            item.setActionText("가입 중");

                            // 어댑터에 반영
                            searchResultAdapter.refreshItem(item);
                        }

                        @Override
                        public void onError(Throwable t) {
                            if (!isAdded()) return;

                            Toast.makeText(requireContext(), item.getTitle() + " 가입에 실패했어요 🥲", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // TODO: 사용자 팔로우
                    long memberId = item.getId();

                    // -------------------------
                    // UNFOLLOW
                    // -------------------------
                    if (Boolean.TRUE.equals(item.getIsFollowing())) {

                        followRepository.unfollowMember(memberId, new Callback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                if (!isAdded()) return;

                                item.setIsFollowing(false);
                                item.setActionText("팔로우");
                                searchResultAdapter.refreshItem(item);

                                Toast.makeText(requireContext(), item.getTitle() + " 님을 언팔로우했어요.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Exception e) {
                                if (!isAdded()) return;
                                Toast.makeText(requireContext(), "언팔로우 실패", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    // -------------------------
                    // FOLLOW
                    // -------------------------
                    else {

                        followRepository.followMember(memberId, new org.maru.muaring.core.common.Callback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                if (!isAdded()) return;

                                Toast.makeText(requireContext(), item.getTitle() + " 님을 팔로우했어요! 🎵", Toast.LENGTH_SHORT).show();

                                item.setIsFollowing(true);
                                item.setActionText("팔로잉");
                                searchResultAdapter.refreshItem(item);
                            }

                            @Override
                            public void onError(Exception e) {
                                if (!isAdded()) return;
                                Toast.makeText(requireContext(), "팔로우에 실패했어요 🥲", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
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

                // 토글 바꾸면 검색어 없애고 추천 모드로 복귀
                if (searchBarFragment != null) {
                    searchBarFragment.clearQuery();
                    searchBarFragment.clearFocus();
                }
                searchHasFocus = false;
                loadRecommendationsByToggle();
            }
            @Override
            public void onUserSelected() {
                updateUIForUserSearch();
                clearSearchResults();

                if (searchBarFragment != null) {
                    searchBarFragment.clearQuery();
                    searchBarFragment.clearFocus();
                }
                searchHasFocus = false;
                loadRecommendationsByToggle();
            }
        });

        // 검색바 콜백
        if (searchBarFragment != null) {
            searchBarFragment.setOnSearchClickListener(query -> performSearch(query));

            searchBarFragment.setOnFocusChangeListener(hasFocus -> {
                searchHasFocus = hasFocus;

                if (hasFocus) {
                    // 검색창 활성화(입력 모드) → 추천은 무조건 숨김
                    clearSearchResults();
                    showSearchUi(false);
                    return;
                }

                // 포커스 해제 + 검색어 비었으면 → 추천 복귀
                if (searchBarFragment.getQuery().trim().isEmpty()) {
                    loadRecommendationsByToggle();
                }
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
        if (query == null) query = "";

        if (query.trim().isEmpty()) {
            clearSearchResults();

            if (!searchHasFocus) {
                loadRecommendationsByToggle();
            } else {
                showSearchUi(false);
            }
            return;
        }

        // 검색어 존재 → 추천 숨김, 검색 결과 모드
        showSearchUi(false);

        if (segmentedToggleView.isGroupSelected()) {
            searchGroups(query);
        } else {
            searchMembers(query);
        }
    }

    private void searchGroups(String query) {
        groupRepository.searchGroups(query, 0, 10,
                new GroupRepository.SearchGroupsCallback() {
                    @Override
                    public void onSuccess(List<GroupSummary> groups) {
                        if (!isAdded()) return;

                        if (groups == null || groups.isEmpty()) {
                            updateEmptyView(true);
                            searchResultAdapter.clear();
                            return;
                        }
                        updateEmptyView(false);

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
                                    joined,
                                    null,
                                    g.getImageUrl()
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


    private void searchMembers(String query) {
        // TODO: 사용자 검색 API 연동 후 SearchResultItem.Type.USER 로 매핑

        memberRepository.searchMembers(query, 0, 10,
                new MemberRepository.SearchMembersCallback() {
                    @Override
                    public void onSuccess(List<MemberSearchItemDto> members) {
                        if (!isAdded()) return;

                        if (members == null || members.isEmpty()) {
                            updateEmptyView(true);
                            searchResultAdapter.clear();
                            return;
                        }
                        updateEmptyView(false);

                        List<SearchResultItem> items = new ArrayList<>();

                        for (MemberSearchItemDto m : members) {

                            // 오늘의 음악 한 줄 만들기
                            String today = null;
                            if (m.getTodayMusicName() != null && m.getTodayMusicArtistName() != null) {
                                today = m.getTodayMusicName() + " - " + m.getTodayMusicArtistName();
                            }

                            boolean following = Boolean.TRUE.equals(m.getIsFollowing());
                            String actionText = following ? "팔로잉" : "팔로우";

                            items.add(new SearchResultItem(
                                    SearchResultItem.Type.USER,     // 멤버 타입
                                    m.getMemberId(),                  // id
                                    m.getNickname(),                  // 제목(닉네임)
                                    null,                             // 카테고리 없음 (그룹 전용)
                                    today,                            // 오늘의 음악 텍스트
                                    actionText,                       // 버튼 텍스트
                                    null,                             // isJoined (그룹 전용)
                                    following,                        // isFollowing (멤버 전용)
                                    m.getProfileImageUrl()
                            ));
                        }

                        searchResultAdapter.setItems(items);
                    }

                    @Override
                    public void onError(Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(requireContext(),
                                "멤버 검색 실패", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateEmptyView(boolean isEmpty) {
        if (isEmpty) {
            tvEmptyResult.setVisibility(View.VISIBLE);
            recyclerSearchResult.setVisibility(View.GONE);
        } else {
            tvEmptyResult.setVisibility(View.GONE);
            recyclerSearchResult.setVisibility(View.VISIBLE);
        }
    }

    private void showRecommendUi() {
        tvRecommendTitle.setVisibility(View.VISIBLE);
        tvEmptyResult.setVisibility(View.GONE);
        recyclerSearchResult.setVisibility(View.VISIBLE);
    }

    private void showSearchUi(boolean empty) {
        tvRecommendTitle.setVisibility(View.GONE);

        if (empty) {
            tvEmptyResult.setVisibility(View.VISIBLE);
            recyclerSearchResult.setVisibility(View.GONE);
        } else {
            tvEmptyResult.setVisibility(View.GONE);
            recyclerSearchResult.setVisibility(View.VISIBLE);
        }
    }

    private void loadRecommendationsByToggle() {
        showRecommendUi();

        if (segmentedToggleView.isGroupSelected()) {
            tvRecommendTitle.setText("뮤어링님이 좋아할 그룹");
            loadGroupRecommendations();
        } else {
            tvRecommendTitle.setText("뮤어링님이 좋아할 사용자");
            loadMemberRecommendations();
        }
    }

    // TODO: 추천 API 연동 완료 후 제거 (UI 확인용 더미 데이터)
    private void loadGroupRecommendations() {
        if (searchResultAdapter == null) return;

        // ====== UI 확인용 더미 데이터 ======
        List<SearchResultItem> items = new ArrayList<>();

        items.add(new SearchResultItem(
                SearchResultItem.Type.GROUP,
                1L,
                "인디 음악 좋아하는 사람들",
                List.of("인디", "감성", "밴드"),
                null,
                "가입",
                false,
                null,
                null
        ));

        items.add(new SearchResultItem(
                SearchResultItem.Type.GROUP,
                2L,
                "힙합 덕후 모임",
                List.of("힙합", "랩", "비트"),
                null,
                "가입",
                false,
                null,
                null
        ));

        searchResultAdapter.setItems(items);
    }

    // TODO: 추천 API 연동 완료 후 제거 (UI 확인용 더미 데이터)
    private void loadMemberRecommendations() {
        if (searchResultAdapter == null) return;

        // ====== UI 확인용 더미 데이터 ======
        List<SearchResultItem> items = new ArrayList<>();

        items.add(new SearchResultItem(
                SearchResultItem.Type.USER,
                101L,
                "뮤어링",
                null,
                "LOVE DIVE - IVE",
                "팔로우",
                null,
                false,
                null
        ));

        items.add(new SearchResultItem(
                SearchResultItem.Type.USER,
                102L,
                "헤헤",
                null,
                null,   // 오늘의 음악 없음
                "팔로우",
                null,
                false,
                null
        ));

        searchResultAdapter.setItems(items);
    }

//    private void loadGroupRecommendations() {
//        recommendationRepository.getGroupRecommendations(
//                20, new Callback<GroupRecommendListResponseDto>() {
//            @Override
//            public void onSuccess(GroupRecommendListResponseDto dto) {
//                if (!isAdded()) return;
//
//                List<SearchResultItem> items = new ArrayList<>();
//                for (var g : dto.getGroups()) {
//                    boolean joined = Boolean.TRUE.equals(g.getIsJoined());
//
//                    items.add(new SearchResultItem(
//                            SearchResultItem.Type.GROUP,
//                            g.getGroupId(),
//                            g.getName(),
//                            g.getCategoryNames(),
//                            null,
//                            joined ? "가입 중" : "가입",
//                            joined,
//                            null,
//                            g.getImgUrl()
//                    ));
//                }
//                searchResultAdapter.setItems(items);
//            }
//
//            @Override
//            public void onError(Exception e) {
//                Toast.makeText(requireContext(), "그룹 추천 실패", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

//    private void loadMemberRecommendations() {
//        recommendationRepository.getMemberRecommendations(
//                20, new Callback<List<MemberRecommendItemDto>>() {
//            @Override
//            public void onSuccess(List<MemberRecommendItemDto> members) {
//                if (!isAdded()) return;
//
//                List<SearchResultItem> items = new ArrayList<>();
//                for (MemberRecommendItemDto m : members) {
//                    String today = null;
//                    if (m.getTodayMusicName() != null && m.getTodayMusicArtistName() != null) {
//                        today = m.getTodayMusicName() + " - " + m.getTodayMusicArtistName();
//                    }
//
//                    boolean following = Boolean.TRUE.equals(m.getIsFollowing());
//
//                    items.add(new SearchResultItem(
//                            SearchResultItem.Type.USER,
//                            m.getMemberId(),
//                            m.getNickname(),
//                            null,
//                            today,
//                            following ? "팔로잉" : "팔로우",
//                            null,
//                            following,
//                            m.getProfileImageUrl()
//                    ));
//                }
//                searchResultAdapter.setItems(items);
//            }
//
//            @Override
//            public void onError(Exception e) {
//                Toast.makeText(requireContext(), "사용자 추천 실패", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }


}