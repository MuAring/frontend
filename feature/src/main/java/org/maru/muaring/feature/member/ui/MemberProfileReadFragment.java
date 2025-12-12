package org.maru.muaring.feature.member.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import org.maru.muaring.data.api.dto.MemberProfileReadResponse;
import org.maru.muaring.feature.R;
import org.maru.muaring.feature.history.ui.adapter.MusicHistoryAdapter;
import org.maru.muaring.feature.history.ui.calendar.MusicHistoryCalendarView;
import org.maru.muaring.feature.history.ui.model.MusicHistoryItem;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MemberProfileReadFragment extends Fragment {

    private MusicHistoryAdapter historyAdapter;
    private MemberProfileReadViewModel viewModel;
    private ImageView profileImage;
    private TextView profileName;
    private TextView sharedMusicCount;
    private TextView followerCount;
    private TextView followingCount;
    private TextView joinedGroupCount;
//    private View privateLayout;
    private LinearLayout historySection;
    private TextView textHistoryMonth;
    private ImageView btnMonthPrev;
    private ImageView btnMonthNext;
    private ImageView btnHistoryList;
    private ImageView btnHistoryCalendar;
    private RecyclerView recyclerHistory;
    private MusicHistoryCalendarView calendarView;
    private int currentYear;
    private int currentMonth;

    private Long memberId; // 네비게이션으로 전달받음

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_member_profile_read, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(MemberProfileReadViewModel.class);
        calendarView = view.findViewById(R.id.include_history).findViewById(R.id.view_music_history_calendar);

        Bundle args = getArguments();
        if (args != null && args.containsKey("memberId")) {
            memberId = args.getLong("memberId");
        } else {
            memberId = -1L;
        }

        bindViews(view);
        setupHistoryRecycler(view);
        initHistorySection(view);
        initCurrentYearMonth();
        updateMonthText();
        observeProfile();
        observeHistory();
        showListMode();
        loadHistory();
        if (memberId != -1L) {
            viewModel.loadMemberProfile(memberId);
        }
    }

    private void bindViews(View v) {
        profileImage = v.findViewById(R.id.image_profile);
        profileName = v.findViewById(R.id.text_profile_name);
//        privateLayout = v.findViewById(R.id.layout_private_account);
        historySection = v.findViewById(R.id.include_history);
        sharedMusicCount = v.findViewById(R.id.text_stat_shared);
        followerCount = v.findViewById(R.id.text_stat_follower);
        followingCount = v.findViewById(R.id.text_stat_following);
        joinedGroupCount = v.findViewById(R.id.text_stat_group);
    }

    private void setupHistoryRecycler(View root) {
        RecyclerView recyclerView = root.findViewById(R.id.recycler_history);

        historyAdapter = new MusicHistoryAdapter(item -> {
            // 클릭 시 postId, musicId 갖고 상세 화면으로 이동할 때 쓰기
            Long postId = item.getPostId();
            Long musicId = item.getMusicId();
            // TODO: NavController로 이동
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(historyAdapter);
    }

    private void observeProfile() {
        viewModel.getMemberProfile().observe(getViewLifecycleOwner(), res -> {
            if (res == null) return;

            switch (res.status) {
                case LOADING:
                    // 로딩 UI 필요하면 처리
                    break;

                case SUCCESS:
                    MemberProfileReadResponse data = res.data;
                    if (data != null) {
                        updateProfileUI(data);
                    }
                    break;

                case ERROR:
                    // 에러 표시
                    break;
            }
        });
    }

    private void updateProfileUI(MemberProfileReadResponse profile) {
        profileName.setText(profile.getNickname());
        sharedMusicCount.setText(String.valueOf(profile.getSharedMusicCount()));
        followerCount.setText(String.valueOf(profile.getFollowerCount()));
        followingCount.setText(String.valueOf(profile.getFolloweeCount()));
        joinedGroupCount.setText(String.valueOf(profile.getJoinedGroupCount()));

//        // 타인 프로필 + 비공개 계정 + 팔로우 안함 → 위 레이아웃 보여주기
//        if (!profile.isMe() && !profile.isPublic() && !profile.isFollowing()) {
//            privateLayout.setVisibility(View.VISIBLE);
//            historySection.setVisibility(View.GONE);
//            return;
//        }
//        privateLayout.setVisibility(View.GONE);
        historySection.setVisibility(View.VISIBLE);

        Glide.with(profileImage.getContext())
                .load(profile.getImageUrl())
                .centerCrop()
                .into(profileImage);
    }

    private void observeHistory() {
        viewModel.getMemberHistory().observe(getViewLifecycleOwner(), res -> {
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
        calendarView.loadMonth(currentYear, currentMonth);
        loadHistory();
    }


    private void updateMonthText() {
        String text = String.format(Locale.KOREA, "%d년 %d월", currentYear, currentMonth);
        textHistoryMonth.setText(text);
    }

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

    private void loadHistory() {
        if (memberId == null || memberId <= 0) return;
        viewModel.loadMemberHistory(memberId, currentYear, currentMonth, 0);
    }

    private void updateHistory(List<MusicHistoryItem> items) {
        historyAdapter.setItems(items);
        calendarView.setItems(items);
    }
}