package org.maru.muaring.feature.follow.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.maru.muaring.feature.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FollowFragment extends Fragment {
    private RecyclerView rvFollowList;
    private FollowAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_follow_list, container, false);
        rvFollowList = view.findViewById(R.id.rvFollowList);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FollowToggleView toggleView = view.findViewById(R.id.followToggle);

        rvFollowList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FollowAdapter(new ArrayList<>());
        rvFollowList.setAdapter(adapter);

        showFollowerList();

        toggleView.setOnSegmentSelectedListener(new FollowToggleView.OnSegmentSelectedListener() {
            @Override
            public void onFollowerSelected() {
                showFollowerList();
            }

            @Override
            public void onFollowingSelected() {
                showFollowingList();
            }
        });
    }

    private void showFollowerList() {
        List<FollowUser> followerList = Arrays.asList(
                new FollowUser("김예은", "Drowning", "WOODZ"),
                new FollowUser("팔로워2", "Hype boy", "NewJeans")
        );
        adapter.updateList(followerList);
    }

    private void showFollowingList() {
        List<FollowUser> followingList = Arrays.asList(
                new FollowUser("내가팔로우", "Super Shy", "NewJeans"),
                new FollowUser("또다른 팔로잉", "Seven", "JK")
        );
        adapter.updateList(followingList);
    }
}
