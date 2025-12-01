package org.maru.muaring.feature.today.ui;

import android.os.Bundle;
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
import org.maru.muaring.feature.today.model.TodayPostsViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TodayPostsFragment extends Fragment implements TodayPostsAdapter.Listener {

    private static final String ARG_GROUP_ID = "arg_group_id";

    private TodayPostsViewModel viewModel;
    private TodayPostsAdapter adapter;
    private Long groupId; // null 이면 "나" 모드

    public static TodayPostsFragment newInstance(@Nullable Long groupId) {
        TodayPostsFragment fragment = new TodayPostsFragment();
        Bundle args = new Bundle();
        if (groupId != null) {
            args.putLong(ARG_GROUP_ID, groupId);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_today_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(ARG_GROUP_ID)) {
            groupId = getArguments().getLong(ARG_GROUP_ID);
        } else {
            groupId = null;
        }

        RecyclerView rv = view.findViewById(R.id.rvTodayPosts);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TodayPostsAdapter(this);
        rv.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(TodayPostsViewModel.class);

        viewModel.posts.observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            switch (resource.status) {

                case SUCCESS:
                    adapter.submitList(resource.data);
                    break;

                case ERROR:
                    Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                    break;

                case LOADING:
                    // TODO: 로딩 UI 넣고 싶으면 여기
                    break;
            }
        });

        if (groupId == null) {
            viewModel.loadForMe();
        } else {
            viewModel.loadForGroup(groupId);
        }
    }

    @Override
    public void onPostClicked(MusicPostFeedResponse post) {
        // TODO: 상세 화면 이동
    }

    @Override
    public void onPlayClicked(MusicPostFeedResponse post) {
        // TODO: 플레이어 호출
    }

    @Override
    public void onAddMusicClicked(MusicPostFeedResponse post) {
        // TODO: 보관함에 음악 추가하기 버튼 클릭 처리
    }

    @Override
    public void onLikeClicked(MusicPostFeedResponse post) {
        // TODO: 좋아요 처리
    }

    @Override
    public void onCommentClicked(MusicPostFeedResponse post) {
        // TODO: 댓글 눌렀을 때 처리
    }

}
