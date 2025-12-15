package org.maru.muaring.feature.group.ui.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.dto.GroupProfileResponse;
import org.maru.muaring.feature.R;
import org.maru.muaring.feature.history.ui.adapter.MusicHistoryAdapter;
import org.maru.muaring.feature.history.ui.calendar.MusicHistoryCalendarView;
import org.maru.muaring.feature.history.ui.model.MusicHistoryItem;
import org.maru.muaring.data.api.dto.TodayMusicPostResponse;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GroupProfileFragment extends Fragment {

    private GroupProfileViewModel viewModel;

    // Toolbar
    private TextView toolbarTitle;
    private ImageButton toolbarBack;

    // 가입 버튼
    private ImageButton toolbarAction;

    // Profile UI
    private LinearLayout layoutCategoryContainer;
    private ImageView imageProfile;
    private TextView textProfileName;
    private TextView textGroupDescription;

    private TextView textStatGroupLevel;
    private TextView textStatSharedMusic;
    private TextView textStatArchive;
    private TextView textStatMemberCount;

    // 공유한 음악 섹션(칸 전체)
    private LinearLayout layoutSharedMusicSection;

    // 그룹 오늘 공유한 음악 UI
    private View includeTodayShared;
    private ImageView ivAlbumCover;
    private TextView tvSongTitle;
    private TextView tvArtistName;
    private TextView tvLikeCount;
    private TextView tvCommentCount;
    private ImageView btnAdd;
    private androidx.cardview.widget.CardView cardTodayMusic;
    private LinearLayout layoutEmptyState;

    // History UI
    private TextView textHistoryMonth;
    private ImageView btnMonthPrev;
    private ImageView btnMonthNext;
    private ImageView btnHistoryList;
    private ImageView btnHistoryCalendar;
    private RecyclerView recyclerHistory;
    private MusicHistoryCalendarView calendarView;
    private MusicHistoryAdapter historyAdapter;

    // Month state
    private int currentYear;
    private int currentMonth;

    // Group ID (전달받음)
    private Long groupId;
    private Boolean isJoined = false;  // 가입 상태 저장

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("DEBUG", "Host Activity = " + requireActivity().getClass().getSimpleName());
        Log.d("DEBUG", "NavController exists = " + (NavHostFragment.findNavController(this) != null));
        // 백스택 확인
        try {
            NavController nav = NavHostFragment.findNavController(this);
            Log.d("DEBUG", "BackStack count = " + nav.getCurrentBackStackEntry());
        } catch (Exception e) {
            Log.e("DEBUG", "NavController error", e);
        }
        // === groupId 전달 받기 (네비게이션 또는 Bundle 등) ===
        if (getArguments() != null) {
            if (!getArguments().containsKey("groupId")) {
                throw new IllegalStateException("groupId가 전달되지 않았습니다.");
            }
            groupId = getArguments().getLong("groupId");
        } else {
            throw new IllegalStateException("GroupProfileFragment의 인자값이 없습니다.");
        }

        viewModel = new ViewModelProvider(this).get(GroupProfileViewModel.class);

        initToolbar(view);
        initProfileSection(view);
        initTodayMusicSection(view);
        initHistorySection(view);
        initCurrentYearMonth();
        updateMonthText();

        observeProfile();
        observeTodayMusic();
        observeHistory();
        observeJoinStatus();

        // 기본은 리스트 모드
        showListMode();

        // 첫 로딩
        viewModel.loadGroupProfile(groupId);
        viewModel.loadTodayMusic(groupId);
        loadHistory();
    }

    // ====================== Toolbar ======================
    private void initToolbar(@NonNull View root) {
        View toolbar = root.findViewById(R.id.include_toolbar_group);
        if (toolbar == null) return;

        toolbarTitle = toolbar.findViewById(org.maru.muaring.core.R.id.toolbar_title);
        toolbarBack = toolbar.findViewById(R.id.btn_back);
        toolbarAction = toolbar.findViewById(org.maru.muaring.core.R.id.toolbar_action);

        toolbarTitle.setText("그룹 프로필");
        toolbarBack.setOnClickListener(v -> requireActivity().onBackPressed());
        // 가입 버튼 기본 설정
        toolbarAction.setVisibility(View.VISIBLE);
        toolbarAction.setOnClickListener(v -> handleJoinButtonClick());
    }

    // 가입 버튼 클릭 처리
    private void handleJoinButtonClick() {
        if (groupId == null || groupId <= 0) {
            Toast.makeText(requireContext(), "그룹 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isJoined) {
            Toast.makeText(requireContext(), "이미 가입한 그룹입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 가입 확인 다이얼로그 표시
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("그룹 가입")
                .setMessage("이 그룹에 가입하시겠습니까?")
                .setPositiveButton("가입", (dialog, which) -> {
                    viewModel.joinPublicGroup(groupId);
                })
                .setNegativeButton("취소", null)
                .show();
    }

    // ====================== Profile Section ======================
    private void initProfileSection(@NonNull View root) {
        imageProfile = root.findViewById(R.id.image_profile);
        textProfileName = root.findViewById(R.id.text_profile_name);

        layoutCategoryContainer = root.findViewById(R.id.layout_category_container);
        textGroupDescription = root.findViewById(R.id.text_group_description);

        textStatGroupLevel = root.findViewById(R.id.text_stat_group_level);
        textStatSharedMusic = root.findViewById(R.id.text_stat_shared_music);
        textStatArchive = root.findViewById(R.id.text_stat_archive);
        textStatMemberCount = root.findViewById(R.id.text_stat_member_count);

        // 공유한 음악 섹션(칸 전체) 클릭 연결
        layoutSharedMusicSection = root.findViewById(R.id.layout_shared_music_section);
        if (layoutSharedMusicSection != null) {
            layoutSharedMusicSection.setOnClickListener(v -> navigateToSharedMusicList());
        } else {
            // 혹시 id 추가 안 했으면 숫자 텍스트라도 클릭되게 백업
            if (textStatSharedMusic != null) {
                textStatSharedMusic.setOnClickListener(v -> navigateToSharedMusicList());
            }
        }

        // 멤버 섹션 클릭 리스너 추가
        LinearLayout layoutMemberSection = root.findViewById(R.id.layout_member_section);
        if (layoutMemberSection != null) {
            layoutMemberSection.setOnClickListener(v -> navigateToGroupMember());
        }

        // 디버그용
        if (textStatGroupLevel == null) {
            System.out.println("DEBUG >>> textStatGroupLevel is NULL");
        }

    }

    // 공유한 음악 리스트로 이동
    private void navigateToSharedMusicList() {
        if (groupId == null || groupId <= 0) return;

        Bundle args = new Bundle();
        args.putLong("arg_group_id", groupId);

        NavController navController = NavHostFragment.findNavController(this);

        int actionId = getResources().getIdentifier(
                "action_groupProfile_to_groupSharedMusic",
                "id",
                requireContext().getPackageName()
        );
        navController.navigate(actionId, args);
    }

    private void navigateToGroupMember() {
        if (groupId == null || groupId <= 0) {
            Toast.makeText(requireContext(), "그룹 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle args = new Bundle();
        args.putLong("group_id", groupId);

        try {
            NavController navController = NavHostFragment.findNavController(this);
            // 리소스 이름으로 ID 찾기
            int actionId = getResources().getIdentifier(
                    "action_groupProfile_to_groupMember",
                    "id",
                    requireContext().getPackageName()
            );
            navController.navigate(actionId, args);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "화면 전환에 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }


    // ====================== 그룹 오늘 공유한 음악 Section ======================
    private void initTodayMusicSection(@NonNull View root) {
        includeTodayShared = root.findViewById(R.id.include_today_shared);
        if (includeTodayShared == null) return;

        ivAlbumCover = includeTodayShared.findViewById(R.id.ivAlbumCover);
        tvSongTitle = includeTodayShared.findViewById(R.id.tvSongTitle);
        tvArtistName = includeTodayShared.findViewById(R.id.tvArtistName);
        tvLikeCount = includeTodayShared.findViewById(R.id.tvLikeCount);
        tvCommentCount = includeTodayShared.findViewById(R.id.tvCommentCount);
        btnAdd = includeTodayShared.findViewById(R.id.btnAdd);
        cardTodayMusic = includeTodayShared.findViewById(R.id.cardTodayMusic);
        layoutEmptyState = includeTodayShared.findViewById(R.id.layoutEmptyState);

        // 추가 버튼 클릭 리스너
//        if (btnAdd != null) {
//            btnAdd.setOnClickListener(v -> {
//                // TODO: 음악을 내 보관함에 추가하는 기능
//                Toast.makeText(requireContext(), "보관함에 추가", Toast.LENGTH_SHORT).show();
//            });
//        }

        // 카드 전체 클릭 리스너 (게시글 상세로 이동)
        includeTodayShared.setOnClickListener(v -> {
            // TODO: 게시글 상세 페이지로 이동
            Toast.makeText(requireContext(), "게시글 상세 보기", Toast.LENGTH_SHORT).show();
        });
    }

    // ====================== History Section ======================
    private void initHistorySection(@NonNull View root) {
        View historyRoot = root.findViewById(R.id.include_history);
        if (historyRoot == null) return;

        textHistoryMonth = historyRoot.findViewById(R.id.text_history_month);
        btnMonthPrev = historyRoot.findViewById(R.id.image_month_prev);
        btnMonthNext = historyRoot.findViewById(R.id.image_month_next);
        btnHistoryList = historyRoot.findViewById(R.id.image_history_list);
        btnHistoryCalendar = historyRoot.findViewById(R.id.image_history_calendar);
        recyclerHistory = historyRoot.findViewById(R.id.recycler_history);
        calendarView = historyRoot.findViewById(R.id.view_music_history_calendar);

        // Adapter
        historyAdapter = new MusicHistoryAdapter(item -> {
            // TODO: postId 로 상세 페이지 이동
        });

        recyclerHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerHistory.setAdapter(historyAdapter);

        // Month navigation
        btnMonthPrev.setOnClickListener(v -> changeMonth(-1));
        btnMonthNext.setOnClickListener(v -> changeMonth(1));

        // Toggle (list/calendar)
        btnHistoryList.setOnClickListener(v -> showListMode());
        btnHistoryCalendar.setOnClickListener(v -> showCalendarMode());
    }


    // ====================== Month Logic ======================
    private void initCurrentYearMonth() {
        Calendar cal = Calendar.getInstance();
        currentYear = cal.get(Calendar.YEAR);
        currentMonth = cal.get(Calendar.MONTH) + 1;
    }

    private void changeMonth(int delta) {
        currentMonth += delta;

        if (currentMonth < 1) {
            currentMonth = 12;
            currentYear--;
        } else if (currentMonth > 12) {
            currentMonth = 1;
            currentYear++;
        }

        updateMonthText();
        loadHistory();
    }


    private void updateMonthText() {
        String text = String.format(Locale.KOREA, "%d년 %d월", currentYear, currentMonth);
        textHistoryMonth.setText(text);
    }


    // ====================== Toggle ======================
    private void showListMode() {
        recyclerHistory.setVisibility(View.VISIBLE);
        calendarView.setVisibility(View.GONE);

        btnHistoryList.setAlpha(1.0f);
        btnHistoryCalendar.setAlpha(0.4f);
    }

    private void showCalendarMode() {
        recyclerHistory.setVisibility(View.GONE);
        calendarView.setVisibility(View.VISIBLE);

        btnHistoryList.setAlpha(0.4f);
        btnHistoryCalendar.setAlpha(1.0f);
    }


    // ====================== ViewModel 연결: Profile ======================
    private void observeProfile() {
        viewModel.getGroupProfile().observe(getViewLifecycleOwner(), res -> {
            if (res == null) return;

            System.out.println("DEBUG >>> profile status = " + res.status
                    + ", data = " + (res.data != null ? res.data.getName() : "null"));


            if (res.status == Resource.Status.LOADING) {
                // TODO: 로딩 UI
            } else if (res.status == Resource.Status.SUCCESS) {
                try {
                    bindProfile(res.data);
                } catch (Exception e) {
                    e.printStackTrace(); // Logcat에 어디서 터지는지 찍힘
                    Toast.makeText(requireContext(), "프로필 바인딩 중 오류: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            } else if (res.status == Resource.Status.ERROR) {
                Toast.makeText(requireContext(),
                        res.message != null ? res.message : "그룹 프로필 조회 실패",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    // 가입 상태 관찰
    private void observeJoinStatus() {
        viewModel.getJoinStatus().observe(getViewLifecycleOwner(), res -> {
            if (res == null) return;

            switch (res.status) {
                case LOADING:
                    // 로딩 중 버튼 비활성화
                    toolbarAction.setEnabled(false);
                    break;

                case SUCCESS:
                    Toast.makeText(requireContext(), "그룹에 가입되었습니다!", Toast.LENGTH_SHORT).show();
                    toolbarAction.setEnabled(true);
                    // 프로필이 자동으로 다시 로드되어 isJoined가 업데이트됨
                    break;

                case ERROR:
                    String errorMsg = res.message != null ? res.message : "그룹 가입에 실패했습니다.";

                    // 에러 메시지 파싱
                    if (errorMsg.contains("400")) {
                        errorMsg = "이미 가입한 그룹입니다.";
                    } else if (errorMsg.contains("409")) {
                        errorMsg = "그룹이 가득 찼습니다.";
                    } else if (errorMsg.contains("404")) {
                        errorMsg = "그룹을 찾을 수 없습니다.";
                    }

                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    toolbarAction.setEnabled(true);
                    break;
            }
        });
    }

    private void bindProfile(GroupProfileResponse profile) {
        if (profile == null) return;

        // 가입 여부 저장 및 아이콘 업데이트
        isJoined = profile.getIsJoined() != null && profile.getIsJoined();
        updateJoinButtonIcon();

        // 이름
        textProfileName.setText(profile.getName());
//        if (toolbarTitle != null) {
//            toolbarTitle.setText(profile.getName());
//        }

        // 소개글
        if (!TextUtils.isEmpty(profile.getDescription())) {
            textGroupDescription.setText(profile.getDescription());
        } else {
            textGroupDescription.setText("그룹 소개글이 아직 없습니다.");
        }

        // 프로필 이미지
        if (!TextUtils.isEmpty(profile.getImageUrl())) {
            Glide.with(this)
                    .load(profile.getImageUrl())
                    .placeholder(org.maru.muaring.design.R.drawable.ic_default_profile) // 적당한 기본 이미지
                    .error(org.maru.muaring.design.R.drawable.ic_default_profile)
                    .circleCrop()
                    .into(imageProfile);
        }

        // 통계 숫자들
        Integer level = profile.getLevel();                     // 그룹 레벨
        Integer totalPostCount = profile.getTotalPostCount();   // 공유한 음악
        Integer totalMusicCount = profile.getTotalMusicCount(); // 보관함
        Integer memberCount = profile.getMemberCount();         // 멤버 수

        textStatGroupLevel.setText(level != null ? String.valueOf(level) : "-");
        textStatSharedMusic.setText(formatCount(totalPostCount));
        textStatArchive.setText(formatCount(totalMusicCount));
        textStatMemberCount.setText(memberCount != null ? String.valueOf(memberCount) : "-");

        // 카테고리 칩
        if (profile.getGroupCategories() != null &&
                !profile.getGroupCategories().isEmpty()) {

            bindCategoryChips(profile.getGroupCategories());
        } else {
            layoutCategoryContainer.removeAllViews();
            layoutCategoryContainer.setVisibility(View.GONE);
        }

    }

    // 가입 버튼 아이콘 업데이트
    private void updateJoinButtonIcon() {
        if (toolbarAction == null) return;

        if (isJoined) {
            // 가입 완료 상태
            toolbarAction.setImageResource(org.maru.muaring.design.R.drawable.ic_group_al_join);
            toolbarAction.setEnabled(false);  // 이미 가입했으면 클릭 불가
        } else {

            // 미가입 상태
            toolbarAction.setImageResource(org.maru.muaring.design.R.drawable.ic_group_join);
            toolbarAction.setEnabled(true);
        }
    }

    private void bindCategoryChips(List<String> categories) {
        if (layoutCategoryContainer == null) return;

        layoutCategoryContainer.setVisibility(View.VISIBLE);
        layoutCategoryContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(requireContext());

        for (int i = 0; i < categories.size(); i++) {
            String name = categories.get(i);

            View chipView = inflater.inflate(
                    R.layout.item_group_category_chip,
                    layoutCategoryContainer,
                    false
            );
            TextView textChip = chipView.findViewById(R.id.category_chip);
            textChip.setText(name);

            // 오른쪽 마진
            LinearLayout.LayoutParams lp =
                    (LinearLayout.LayoutParams) chipView.getLayoutParams();
            lp.rightMargin = dpToPx(8);
            chipView.setLayoutParams(lp);

            layoutCategoryContainer.addView(chipView);
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }


    private String formatCount(Integer value) {
        if (value == null) return "-";
        int v = value;
        if (v >= 1000) {
            // 5300 -> 5.3K
            float k = v / 1000f;
            return String.format(Locale.US, "%.1fK", k);
        }
        return String.valueOf(v);
    }


    // ====================== ViewModel 연결: 오늘의 음악 ======================
    private void observeTodayMusic() {
        viewModel.getTodayMusic().observe(getViewLifecycleOwner(), res -> {
            if (res == null) return;

            Log.d("DEBUG", "오늘의 음악 status = " + res.status);

            switch (res.status) {
                case LOADING:
                    // 로딩 중에는 둘 다 숨김
                    if (cardTodayMusic != null) cardTodayMusic.setVisibility(View.GONE);
                    if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.GONE);
                    break;

                case SUCCESS:
                    if (res.data != null) {
                        // 데이터가 있으면 카드 표시, 빈 상태 숨김
                        bindTodayMusic(res.data);
                        if (cardTodayMusic != null) cardTodayMusic.setVisibility(View.VISIBLE);
                        if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.GONE);
                        includeTodayShared.setVisibility(View.VISIBLE);
                    } else {
                        // 데이터가 없으면 빈 상태 표시, 카드 숨김
                        if (cardTodayMusic != null) cardTodayMusic.setVisibility(View.GONE);
                        if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.VISIBLE);
                        includeTodayShared.setVisibility(View.VISIBLE);
                    }
                    break;

                case ERROR:
                    Log.e("DEBUG", "오늘의 음악 조회 실패: " + res.message);
                    includeTodayShared.setVisibility(View.GONE);
                    break;
            }
        });
    }

    private void bindTodayMusic(TodayMusicPostResponse post) {
        if (post == null) return;

        // 곡 제목
        if (!TextUtils.isEmpty(post.getMusicName())) {
            tvSongTitle.setText(post.getMusicName());
        }

        // 아티스트 이름
        if (!TextUtils.isEmpty(post.getArtistName())) {
            tvArtistName.setText(post.getArtistName());
        }

        // 앨범 커버 이미지
        if (!TextUtils.isEmpty(post.getAlbumImgUrl())) {
            Glide.with(this)
                    .load(post.getAlbumImgUrl())
                    .placeholder(org.maru.muaring.design.R.drawable.ic_launcher_foreground)
                    .error(org.maru.muaring.design.R.drawable.ic_launcher_foreground)
                    .centerCrop()
                    .into(ivAlbumCover);
        }

        // 좋아요 수
        tvLikeCount.setText(String.valueOf(post.getLikeCount()));

        // 댓글 수
        tvCommentCount.setText(String.valueOf(post.getCommentCount()));

        // 카드 클릭 시 게시글 상세로 이동
        includeTodayShared.setOnClickListener(v -> {
            // TODO: postId를 이용해 게시글 상세 화면으로 이동
            Long postId = post.getPostId();
            Toast.makeText(requireContext(),
                    "게시글 상세 (postId: " + postId + ")",
                    Toast.LENGTH_SHORT).show();
        });
    }

    // ====================== ViewModel 연결: History ======================
    private void loadHistory() {
        if (groupId == null || groupId <= 0) return;
        viewModel.loadGroupHistory(groupId, currentYear, currentMonth, 0);
    }

    private void observeHistory() {
        viewModel.getGroupHistory().observe(getViewLifecycleOwner(), res -> {
            if (res == null) return;

            switch (res.status) {
                case LOADING:
                    // TODO: 로딩 UI
                    break;

                case SUCCESS:
                    List<MusicHistoryItem> list = res.data;
                    updateHistory(list);
                    break;

                case ERROR:
                    Toast.makeText(requireContext(),
                            res.message != null ? res.message : "히스토리 조회 실패",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    // ====================== UI 업데이트 ======================
    private void updateHistory(List<MusicHistoryItem> items) {
        historyAdapter.setItems(items);
        calendarView.setItems(items);
    }
}
