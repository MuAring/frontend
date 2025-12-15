package org.maru.muaring.feature.today.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.maru.muaring.data.api.dto.MusicPostFeedResponse;
import org.maru.muaring.feature.R;
import org.maru.muaring.feature.search.ui.SearchNavigator;
import org.maru.muaring.feature.today.model.TodayPostsViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TodayPostsFragment extends Fragment implements TodayPostsAdapter.Listener {

    private static final String ARG_GROUP_ID = "arg_group_id";
    private static final String ARG_SHOW_SECTION_TITLE = "arg_show_section_title";
    private static final String ARG_ME_TODAY_MODE = "arg_me_today_mode";
    private static final String ARG_USE_PROFILE_LAYOUT = "arg_use_profile_layout";

    private TodayPostsViewModel viewModel;
    private TodayPostsAdapter adapter;

    private Long groupId = null;
    private boolean meTodayMode = false;
    private boolean useMemberProfileLayout = false;

    private SearchNavigator navigator;

    // 1) 홈: 기존 xml + 제목 보임 + followee today
    public static TodayPostsFragment newInstanceForHome() {
        TodayPostsFragment f = new TodayPostsFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_SHOW_SECTION_TITLE, true);
        args.putBoolean(ARG_ME_TODAY_MODE, false);
        args.putBoolean(ARG_USE_PROFILE_LAYOUT, false);
        f.setArguments(args);
        return f;
    }

    // 2) 그룹: 기존 xml + 제목 보임 + group today
    public static TodayPostsFragment newInstanceForGroup(@NonNull Long groupId) {
        TodayPostsFragment f = new TodayPostsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_GROUP_ID, groupId);
        args.putBoolean(ARG_SHOW_SECTION_TITLE, true);
        args.putBoolean(ARG_ME_TODAY_MODE, false);
        args.putBoolean(ARG_USE_PROFILE_LAYOUT, false);
        f.setArguments(args);
        return f;
    }

    // 3) 사용자 프로필(공유한 음악): 프로필 xml + 제목 설정 + meTodayMode에 따라 API 분기
    public static TodayPostsFragment newInstanceForProfile(boolean showSectionTitle,
                                                           boolean meTodayMode) {
        TodayPostsFragment f = new TodayPostsFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_SHOW_SECTION_TITLE, showSectionTitle);
        args.putBoolean(ARG_ME_TODAY_MODE, meTodayMode);
        args.putBoolean(ARG_USE_PROFILE_LAYOUT, true);
        f.setArguments(args);
        return f;
    }

    // 4) 그룹 프로필(공유한 음악): 프로필 xml + 제목 설정 + meTodayMode에 따라 API 분기
    public static TodayPostsFragment newInstanceForGroupProfile(@NonNull Long groupId) {
        TodayPostsFragment f = new TodayPostsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_GROUP_ID, groupId);
        args.putBoolean(ARG_SHOW_SECTION_TITLE, false);
        args.putBoolean(ARG_ME_TODAY_MODE, false);
        args.putBoolean(ARG_USE_PROFILE_LAYOUT, true); // profile xml
        f.setArguments(args);
        return f;
    }


    // =========================

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof SearchNavigator) {
            navigator = (SearchNavigator) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        navigator = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Bundle args = getArguments();
        if (args != null) {
            useMemberProfileLayout = args.getBoolean(ARG_USE_PROFILE_LAYOUT, false);
        } else {
            useMemberProfileLayout = false;
        }

        int layoutId = useMemberProfileLayout
                ? R.layout.fragment_today_posts_for_profile
                : R.layout.fragment_today_posts;

        return inflater.inflate(layoutId, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("TodayFragment", "onViewCreated called!");

        // ===== args 파싱 =====
        Bundle args = getArguments();
        if (args != null) {
            meTodayMode = args.getBoolean(ARG_ME_TODAY_MODE, false);

            if (args.containsKey(ARG_GROUP_ID)) {
                long gid = args.getLong(ARG_GROUP_ID, -1L);
                groupId = (gid > 0) ? gid : null;
            } else {
                groupId = null;
            }
        } else {
            meTodayMode = false;
            groupId = null;
        }

        // 그룹이 최우선
        if (groupId != null) meTodayMode = false;

        // ===== RV 세팅 =====
        RecyclerView rv = view.findViewById(R.id.rvTodayPosts);
        rv.setNestedScrollingEnabled(false);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TodayPostsAdapter(this);
        rv.setAdapter(adapter);

        // ===== VM =====
        viewModel = new ViewModelProvider(this).get(TodayPostsViewModel.class);

        viewModel.posts.observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            Log.d("TodayFragment", "observe posts, status=" + resource.status);

            switch (resource.status) {
                case SUCCESS:
                    adapter.submitList(resource.data);
                    break;

                case ERROR:
                    Log.e("TodayFragment", "ERROR: " + resource.message);
                    Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                    break;

                case LOADING:
                    Log.d("TodayFragment", "LOADING...");
                    break;
            }
        });

        // ===== 섹션 타이틀 show/hide =====
        boolean showSectionTitle = true;
        if (args != null) {
            showSectionTitle = args.getBoolean(ARG_SHOW_SECTION_TITLE, true);
        }

        View titleView = view.findViewById(R.id.tvSectionTitle);
        if (titleView != null) {
            titleView.setVisibility(showSectionTitle ? View.VISIBLE : View.GONE);
        }

        // ===== 로딩 분기 =====
        if (groupId != null && useMemberProfileLayout) {
            viewModel.loadForGroupProfile(groupId);    // /groups/{id}/posts
        } else if (groupId != null) {
            viewModel.loadForGroup(groupId);           // /groups/{id}/posts/today
        } else if (meTodayMode) {
            viewModel.loadForMeToday();                // /post/me
        } else {
            viewModel.loadForMe();                     // /post/followee/today
        }

    }

    // ============================================
    // =========== Adapter Listener 구현 ===========
    // ============================================

    @Override
    public void onPostClicked(MusicPostFeedResponse post) {
        if (navigator != null && post != null) {
            navigator.navigateToPostDetail(post.getPostId());
        }
    }

    @Override
    public void onPlayClicked(MusicPostFeedResponse post) {
        // TODO
    }

    @Override
    public void onLikeClicked(MusicPostFeedResponse post) {
        if (post != null && post.getPostId() != null) {
            viewModel.toggleLike(post.getPostId());
        }
    }

    @Override
    public void onCommentClicked(MusicPostFeedResponse post) {
        // TODO
    }

    @Override
    public void onLibraryClick(MusicPostFeedResponse post) {
        if (post == null || post.getMusicId() == null) return;

        Log.d("TodayFragment", "onLibraryClick musicId=" + post.getMusicId()
                + " inLibrary=" + post.isInLibrary());

        if (post.isInLibrary()) {
            viewModel.removeFromLibrary(post.getMusicId());
        } else {
            viewModel.addToLibrary(post.getMusicId(), null);
        }
    }
}


//package org.maru.muaring.feature.today.ui;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.ViewModelProvider;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import org.maru.muaring.data.api.dto.MusicPostFeedResponse;
//import org.maru.muaring.feature.R;
//import org.maru.muaring.feature.search.ui.SearchNavigator;
//import org.maru.muaring.feature.today.model.TodayPostsViewModel;
//import dagger.hilt.android.AndroidEntryPoint;
//
//@AndroidEntryPoint
//public class TodayPostsFragment extends Fragment implements TodayPostsAdapter.Listener {
//
//    private static final String ARG_GROUP_ID = "arg_group_id";
//    private static final String ARG_SHOW_SECTION_TITLE = "arg_show_section_title";
//    // "내 오늘 게시물" 모드 여부
//    private static final String ARG_ME_TODAY_MODE = "arg_me_today_mode";
//    private static final String ARG_USE_MEMBER_PROFILE_LAYOUT = "arg_use_member_profile_layout";
//
//    private TodayPostsViewModel viewModel;
//    private TodayPostsAdapter adapter;
//    private Long groupId; // null 이면 "나" 모드
//    private boolean meTodayMode = false; //
//    private SearchNavigator navigator;
//
//    public static TodayPostsFragment newInstance(@Nullable Long groupId) {
//        // 기본값: 섹션 타이틀 보여줌
//        return newInstance(groupId, true);
//    }
//
//    public static TodayPostsFragment newInstance(@Nullable Long groupId, boolean showSectionTitle) {
//        TodayPostsFragment fragment = new TodayPostsFragment();
//        Bundle args = new Bundle();
//
//        if (groupId != null) {
//            args.putLong(ARG_GROUP_ID, groupId);
//        }
//        args.putBoolean(ARG_SHOW_SECTION_TITLE, showSectionTitle);
//        args.putBoolean(ARG_ME_TODAY_MODE, false);  // 기본은 기존 동작 유지
//
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    public static TodayPostsFragment newInstanceForMemberProfile(boolean showSectionTitle) {
//        TodayPostsFragment f = new TodayPostsFragment();
//        Bundle args = new Bundle();
//        args.putBoolean(ARG_SHOW_SECTION_TITLE, showSectionTitle);
//        args.putBoolean(ARG_USE_MEMBER_PROFILE_LAYOUT, true);
//        f.setArguments(args);
//        return f;
//    }
//
//    // MemberSharedMusicFragment에서 쓸 팩토리
//    public static TodayPostsFragment newInstanceMeToday(boolean showSectionTitle) {
//        TodayPostsFragment fragment = new TodayPostsFragment();
//        Bundle args = new Bundle();
//        args.putBoolean(ARG_SHOW_SECTION_TITLE, showSectionTitle);
//        args.putBoolean(ARG_ME_TODAY_MODE, true);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        if (context instanceof SearchNavigator) {
//            navigator = (SearchNavigator) context;
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        navigator = null;
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//
//        boolean useMemberLayout = false;
//        if (getArguments() != null) {
//            useMemberLayout = getArguments().getBoolean(ARG_USE_MEMBER_PROFILE_LAYOUT, false);
//        }
//
//        int layoutId = useMemberLayout
//                ? R.layout.fragment_today_posts_member_profile
//                : R.layout.fragment_today_posts;
//
//        return inflater.inflate(layoutId, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view,
//                              @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        Log.d("TodayFragment", "onViewCreated called!");
//
//        Bundle args = getArguments();
//
//        if (args != null) {
//            meTodayMode = args.getBoolean(ARG_ME_TODAY_MODE, false);
//
//            if (args.containsKey(ARG_GROUP_ID)) {
//                long gid = args.getLong(ARG_GROUP_ID, -1L);
//                groupId = (gid > 0) ? gid : null;
//            } else {
//                groupId = null;
//            }
//        } else {
//            meTodayMode = false;
//            groupId = null;
//        }
//
//        // groupId가 있으면 그룹 모드가 최우선
//        if (groupId != null) {
//            meTodayMode = false;
//        }
//
//        RecyclerView rv = view.findViewById(R.id.rvTodayPosts);
//        rv.setNestedScrollingEnabled(false);
//        rv.setLayoutManager(new LinearLayoutManager(getContext()));
//        adapter = new TodayPostsAdapter(this);
//        rv.setAdapter(adapter);
//
//        viewModel = new ViewModelProvider(this).get(TodayPostsViewModel.class);
//
//        viewModel.posts.observe(getViewLifecycleOwner(), resource -> {
//            if (resource == null) return;
//
//            android.util.Log.d("TodayFragment", "observe posts, status=" + resource.status);
//
//            switch (resource.status) {
//                case SUCCESS:
//                    if (resource.data != null) {
//                        android.util.Log.d("TodayFragment",
//                                "SUCCESS: list size = " + resource.data.size());
//                    } else {
//                        android.util.Log.d("TodayFragment", "SUCCESS: data is null");
//                    }
//                    adapter.submitList(resource.data);
//                    break;
//
//                case ERROR:
//                    android.util.Log.e("TodayFragment", "ERROR: " + resource.message);
//                    Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
//                    break;
//
//                case LOADING:
//                    android.util.Log.d("TodayFragment", "LOADING...");
//                    break;
//            }
//        });
//
//        boolean showSectionTitle = true;
//        if (getArguments() != null) {
//            showSectionTitle = getArguments().getBoolean(ARG_SHOW_SECTION_TITLE, true);
//        }
//
//        View titleView = view.findViewById(R.id.tvSectionTitle);
//        if (titleView != null) {
//            titleView.setVisibility(showSectionTitle ? View.VISIBLE : View.GONE);
//        }
//
//        // ===== 로딩 분기 =====
//        if (groupId != null) {
//            viewModel.loadForGroup(groupId);
//        } else if (meTodayMode) {
//            viewModel.loadForMeToday(); // /post/me/today
//        } else {
//            viewModel.loadForMe();      // 기존 /post/followee/today
//        }
//    }
//
//    // ============================================
//    // =========== Adapter Listener 구현 ===========
//    // ============================================
//
//    @Override
//    public void onPostClicked(MusicPostFeedResponse post) {
//        // TODO: 상세 화면 이동
//        if (navigator != null) {
//            navigator.navigateToPostDetail(post.getPostId());
//        }
//    }
//
//    @Override
//    public void onPlayClicked(MusicPostFeedResponse post) {
//        // TODO: 플레이어 호출
//    }
//
//    @Override
//    public void onLikeClicked(MusicPostFeedResponse post) {
//        // TODO: 좋아요 처리
//        // UI 쪽(아이콘/카운트)는 어댑터에서 반영
//        // 여기서는 백엔드 토글 API만 호출
//        if (post.getPostId() != null) {
//            viewModel.toggleLike(post.getPostId());
//        }
//    }
//
//    @Override
//    public void onCommentClicked(MusicPostFeedResponse post) {
//        // TODO: 댓글 눌렀을 때 처리
//    }
//
//    @Override
//    public void onLibraryClick(MusicPostFeedResponse post) {
//        // UI 쪽(아이콘)은 어댑터에서 반영
//        // 여기서는 보관함 API만 호출
//        if (post.getMusicId() == null) return;
//
//        Log.d("TodayFragment", "onLibraryClick musicId=" + post.getMusicId()
//                + " inLibrary=" + post.isInLibrary());
//
//        if (post.isInLibrary()) {
//            viewModel.removeFromLibrary(post.getMusicId());
//        } else {
//            viewModel.addToLibrary(post.getMusicId(), null);
//        }
//    }
//}