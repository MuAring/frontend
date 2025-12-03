package org.maru.muaring.feature.group.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.maru.muaring.feature.R;
import org.maru.muaring.feature.history.ui.adapter.MusicHistoryAdapter;
import org.maru.muaring.feature.history.ui.calendar.MusicHistoryCalendarView;
import org.maru.muaring.feature.history.ui.decoration.MusicHistoryTimelineDecoration;
import org.maru.muaring.feature.history.ui.model.MusicHistoryItem;
import org.maru.muaring.feature.history.ui.model.HistoryMapper;

import dagger.hilt.android.AndroidEntryPoint;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@AndroidEntryPoint
public class GroupProfileFragment extends Fragment {

    private GroupProfileViewModel viewModel;

    // Toolbar
    private TextView toolbarTitle;
    private ImageButton toolbarBack;
    private ImageButton toolbarAction;

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
            groupId = getArguments().getLong("groupId", -1L);
        }

        viewModel = new ViewModelProvider(this).get(GroupProfileViewModel.class);

        initToolbar(view);
        initHistorySection(view);
        initCurrentYearMonth();
        updateMonthText();

        observeHistory();

        // 기본은 리스트 모드
        showListMode();

        // 첫 로딩
        loadHistory();
    }

    // ====================== Toolbar ======================
    private void initToolbar(@NonNull View root) {
        View toolbar = root.findViewById(R.id.include_toolbar_group);
        if (toolbar == null) return;

        toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarBack = toolbar.findViewById(R.id.btn_back);
        toolbarAction = toolbar.findViewById(R.id.toolbar_action);

        toolbarTitle.setText("그룹 프로필");
        toolbarBack.setOnClickListener(v -> requireActivity().onBackPressed());
        toolbarAction.setVisibility(View.VISIBLE);
        toolbarAction.setOnClickListener(v -> {
            // TODO: 그룹 설정 이동
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
        calendarView = historyRoot.findViewById(R.id.view_history_calendar);

        // Adapter
        historyAdapter = new MusicHistoryAdapter(item -> {
            // TODO: postId 로 상세 페이지 이동
        });

        recyclerHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerHistory.setAdapter(historyAdapter);
        recyclerHistory.addItemDecoration(new MusicHistoryTimelineDecoration(requireContext()));

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

    // ====================== ViewModel 연결 ======================
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
                    // TODO: 에러 토스트
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
