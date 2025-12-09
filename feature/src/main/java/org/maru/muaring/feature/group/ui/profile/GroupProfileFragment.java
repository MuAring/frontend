package org.maru.muaring.feature.group.ui.profile;

import android.os.Bundle;
import android.text.TextUtils;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.dto.GroupProfileResponse;
import org.maru.muaring.feature.R;
import org.maru.muaring.feature.group.ui.GroupMemberFragment;
import org.maru.muaring.feature.history.ui.adapter.MusicHistoryAdapter;
import org.maru.muaring.feature.history.ui.calendar.MusicHistoryCalendarView;
import org.maru.muaring.feature.history.ui.model.MusicHistoryItem;

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
        initHistorySection(view);
        initCurrentYearMonth();
        updateMonthText();

        observeProfile();
        observeHistory();

        // 기본은 리스트 모드
        showListMode();

        // 첫 로딩
        viewModel.loadGroupProfile(groupId);
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
        toolbarAction.setVisibility(View.VISIBLE);
        toolbarAction.setOnClickListener(v -> {
            // TODO: 그룹 설정 이동
        });
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

        // 디버그용
        if (textStatGroupLevel == null) {
            System.out.println("DEBUG >>> textStatGroupLevel is NULL");
        }

        LinearLayout memberSection = root.findViewById(R.id.layout_member_section);
        if (memberSection != null) {
            memberSection.setOnClickListener(v -> navigateToMemberList());
        }

    }

    // 그룹 멤버 조회로 이동 메서드
    private void navigateToMemberList() {
        if (groupId == null) {
            Toast.makeText(requireContext(), "그룹 정보를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
            return;
        }

        GroupMemberFragment fragment = GroupMemberFragment.newInstance(groupId);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)  // 실제 컨테이너 ID로 변경
                .addToBackStack(null)
                .commit();
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


    private void bindProfile(GroupProfileResponse profile) {
        if (profile == null) return;

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
