package org.maru.muaring.feature.member.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import org.maru.muaring.core.TokenManager;
import org.maru.muaring.data.api.dto.MemberProfileReadResponse;
import org.maru.muaring.data.api.dto.TodayPostResponse;
import org.maru.muaring.feature.R;
import org.maru.muaring.feature.history.ui.adapter.MusicHistoryAdapter;
import org.maru.muaring.feature.history.ui.calendar.MusicHistoryCalendarView;
import org.maru.muaring.feature.history.ui.model.MusicHistoryItem;
import org.maru.muaring.feature.search.ui.SearchNavigator;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MemberProfileReadFragment extends Fragment {

    @Inject
    TokenManager tokenManager;
    private MusicHistoryAdapter historyAdapter;
    private MemberProfileReadViewModel viewModel;
    private View toolBar;
    private ImageButton toolBarBtnBack;
    private TextView toolbarTitle;

    // 프로필 정보
    private ImageView profileImage;
    private TextView profileName;

    // 버튼들
    private Button btnEditProfile;
    private Button btnAlarm;
    private Button btnFollow;
    private Button btnFollowed;

    // 통계
    private TextView sharedMusicCount;
    private TextView followerCount;
    private TextView followingCount;
    private TextView joinedGroupCount;

    // 오늘 공유한 음악 카드
    private View todayMusicCard;
    private CardView cardTodayMusic;
    private LinearLayout layoutEmptyState;
    private ImageView todayAlbumImage;
    private TextView todayMusicName;
    private TextView todayArtistName;
    private TextView todayLikeCount;
    private TextView todayCommentCount;

    // 히스토리
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

    private Long memberId;
    private SearchNavigator navigator;

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
        View view = inflater.inflate(R.layout.fragment_member_profile_read, container, false);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnEditProfile.setOnClickListener(v -> {
            if (navigator != null) {
                navigator.navigateToProfileEdit();
            }
        });
        return view;
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
            memberId = tokenManager.getMemberId();
        }

        bindViews(view);
        setupHistoryRecycler(view);
        initHistorySection(view);
        initCurrentYearMonth();
        updateMonthText();
        observeProfile();
        observeHistory();
        observeTodayPost();
        showListMode();
        loadHistory(memberId);
        if (memberId != -1L) {
            viewModel.loadMemberProfile(memberId);
            viewModel.loadTodayPost(memberId);
        }
    }

    private void bindViews(View v) {
        toolBar = v.findViewById(R.id.include_toolbar_group);
        toolBarBtnBack = toolBar.findViewById(R.id.btn_back);
        toolbarTitle = toolBar.findViewById(R.id.toolbar_title);
        profileImage = v.findViewById(R.id.image_profile);
        profileName = v.findViewById(R.id.text_profile_name);
        btnAlarm = v.findViewById(R.id.btn_alarm);
        btnFollow = v.findViewById(R.id.btn_follow);
        btnFollowed = v.findViewById(R.id.btn_followed);
        historySection = v.findViewById(R.id.include_history);
        sharedMusicCount = v.findViewById(R.id.text_stat_shared);
        followerCount = v.findViewById(R.id.text_stat_follower);
        followingCount = v.findViewById(R.id.text_stat_following);
        joinedGroupCount = v.findViewById(R.id.text_stat_group);

        // 오늘 공유한 음악 카드 바인딩
        todayMusicCard = v.findViewById(R.id.include_today_shared);
        android.util.Log.d("TodayPost", "todayMusicCard = " + todayMusicCard);

        if (todayMusicCard != null) {
            cardTodayMusic = todayMusicCard.findViewById(R.id.cardTodayMusic);
            layoutEmptyState = todayMusicCard.findViewById(R.id.layoutEmptyState);

            android.util.Log.d("TodayPost", "cardTodayMusic = " + cardTodayMusic);
            android.util.Log.d("TodayPost", "layoutEmptyState = " + layoutEmptyState);

            todayAlbumImage = todayMusicCard.findViewById(R.id.ivAlbumCover);
            todayMusicName = todayMusicCard.findViewById(R.id.tvSongTitle);
            todayArtistName = todayMusicCard.findViewById(R.id.tvArtistName);
            todayLikeCount = todayMusicCard.findViewById(R.id.tvLikeCount);
            todayCommentCount = todayMusicCard.findViewById(R.id.tvCommentCount);
        } else {
            android.util.Log.e("TodayPost", "todayMusicCard is NULL!");
        }
    }

    private void setupHistoryRecycler(View root) {
        RecyclerView recyclerView = root.findViewById(R.id.recycler_history);

        historyAdapter = new MusicHistoryAdapter(item -> {
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
                    break;

                case SUCCESS:
                    MemberProfileReadResponse data = res.data;
                    if (data != null) {
                        updateProfileUI(data);
                    }
                    break;

                case ERROR:
                    break;
            }
        });
    }

    private void updateProfileUI(MemberProfileReadResponse response) {
        toolBarBtnBack.setVisibility(View.INVISIBLE);
        toolbarTitle.setText("프로필");
        profileName.setText(response.getNickname());
        sharedMusicCount.setText(String.valueOf(response.getSharedMusicCount()));
        followerCount.setText(String.valueOf(response.getFollowerCount()));
        followingCount.setText(String.valueOf(response.getFolloweeCount()));
        joinedGroupCount.setText(String.valueOf(response.getJoinedGroupCount()));

        if (response.isMe()) {
            btnEditProfile.setVisibility(View.VISIBLE);
            btnAlarm.setVisibility(View.VISIBLE);
            btnFollowed.setVisibility(View.GONE);
            btnFollow.setVisibility(View.GONE);
        } else if (response.isFollowing()) {
            btnEditProfile.setVisibility(View.GONE);
            btnAlarm.setVisibility(View.GONE);
            btnFollowed.setVisibility(View.VISIBLE);
            btnFollow.setVisibility(View.GONE);
        } else {
            btnEditProfile.setVisibility(View.GONE);
            btnAlarm.setVisibility(View.GONE);
            btnFollowed.setVisibility(View.GONE);
            btnFollow.setVisibility(View.VISIBLE);
        }

        historySection.setVisibility(View.VISIBLE);

        Glide.with(profileImage.getContext())
                .load(response.getImageUrl())
                .centerCrop()
                .into(profileImage);
    }

    private void observeTodayPost() {
        viewModel.getTodayPost().observe(getViewLifecycleOwner(), res -> {
            android.util.Log.d("TodayPost", "observeTodayPost called, res = " + res);

            if (res == null) {
                android.util.Log.d("TodayPost", "res is null");
                return;
            }
            android.util.Log.d("TodayPost", "status = " + res.status);

            switch (res.status) {
                case LOADING:
                    android.util.Log.d("TodayPost", "LOADING");
                    // 로딩 중에는 둘 다 숨김
                    if (cardTodayMusic != null) {
                        cardTodayMusic.setVisibility(View.GONE);
                    }
                    if (layoutEmptyState != null) {
                        layoutEmptyState.setVisibility(View.GONE);
                    }
                    break;

                case SUCCESS:
                    TodayPostResponse data = res.data;
                    android.util.Log.d("TodayPost", "SUCCESS, data = " + (data != null ? "있음" : "null"));
                    if (data != null) {
                        // 데이터가 있으면 카드 표시
                        updateTodayPostUI(data);
                    } else {
                        // 데이터가 없으면 빈 상태 표시
                        showEmptyState();
                    }
                    break;

                case ERROR:
                    android.util.Log.e("TodayPost", "ERROR: " + res.message);
                    // 에러 시 빈 상태 표시
                    showEmptyState();
                    break;
            }
        });
    }

    private void updateTodayPostUI(TodayPostResponse post) {
        if (cardTodayMusic == null || layoutEmptyState == null) return;

        // 카드 표시, 빈 상태 숨김
        cardTodayMusic.setVisibility(View.VISIBLE);
        layoutEmptyState.setVisibility(View.GONE);

        // 앨범 이미지
        if (todayAlbumImage != null) {
            Glide.with(this)
                    .load(post.getAlbumImageUrl())
                    .placeholder(org.maru.muaring.design.R.drawable.ic_launcher_foreground)
                    .error(org.maru.muaring.design.R.drawable.ic_launcher_foreground)
                    .centerCrop()
                    .into(todayAlbumImage);
        }

        // 음악 정보
        if (todayMusicName != null) {
            todayMusicName.setText(post.getMusicName());
        }
        if (todayArtistName != null) {
            todayArtistName.setText(post.getArtistName());
        }

        // 통계
        if (todayLikeCount != null) {
            todayLikeCount.setText(String.valueOf(post.getLikeCount()));
        }
        if (todayCommentCount != null) {
            todayCommentCount.setText(String.valueOf(post.getCommentCount()));
        }

        // 클릭 리스너
        cardTodayMusic.setOnClickListener(v -> {
            Long postId = post.getPostId();
            // TODO: 포스트 상세 페이지로 이동
            Toast.makeText(requireContext(),
                    "Post ID: " + postId + " 클릭",
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void showEmptyState() {
        android.util.Log.d("TodayPost", "showEmptyState called");
        android.util.Log.d("TodayPost", "cardTodayMusic = " + cardTodayMusic);
        android.util.Log.d("TodayPost", "layoutEmptyState = " + layoutEmptyState);
        if (cardTodayMusic != null && layoutEmptyState != null) {
            cardTodayMusic.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
            android.util.Log.d("TodayPost", "Empty state shown");
        } else {
            android.util.Log.e("TodayPost", "Views are null!");
        }
    }

    private void observeHistory() {
        viewModel.getMemberHistory().observe(getViewLifecycleOwner(), res -> {
            if (res == null) return;

            switch (res.status) {
                case LOADING:
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

        historyAdapter = new MusicHistoryAdapter(item -> {
            // TODO: postId 로 상세 페이지 이동
        });

        recyclerHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerHistory.setAdapter(historyAdapter);

        btnMonthPrev.setOnClickListener(v -> changeMonth(-1));
        btnMonthNext.setOnClickListener(v -> changeMonth(1));

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
        loadHistory(memberId);
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

    private void loadHistory(Long memberId) {
        if (memberId == null || memberId <= 0) return;
        viewModel.loadMemberHistory(memberId, currentYear, currentMonth, 0);
    }

    private void updateHistory(List<MusicHistoryItem> items) {
        historyAdapter.setItems(items);
        calendarView.setItems(items);
    }
}