package org.maru.muaring.feature.follow.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.maru.muaring.core.TokenManager;
import org.maru.muaring.core.common.Callback;
import org.maru.muaring.data.api.dto.FollowListResponse;
import org.maru.muaring.data.repository.FollowRepository;
import org.maru.muaring.feature.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import jakarta.inject.Inject;

@AndroidEntryPoint
public class FollowFragment extends Fragment {
    private RecyclerView rvFollowList;
    private FollowAdapter adapter;
    @Inject
    FollowRepository followRepository;

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

        TokenManager tokenManager = new TokenManager(requireContext());
        long Id = tokenManager.getMemberId();

        followRepository.getFollowers(Id, new Callback<List<FollowListResponse>>() {
            @Override
            public void onSuccess(List<FollowListResponse> result) {

                List<FollowUser> mapped = new ArrayList<>();

                for (FollowListResponse r : result) {
                    mapped.add(new FollowUser(
                            r.getName(),
                            r.getProfileImage(),
                            r.getFollowStatus()
                    ));
                }

                adapter.updateList(mapped);
            }

            @Override
            public void onError(Exception e) {
                Log.e("FollowFragment", "팔로워 불러오기 실패: " + e.getMessage());
            }
        });
    }

    private void showFollowingList() {

        TokenManager tokenManager = new TokenManager(requireContext());
        long Id = tokenManager.getMemberId();

        followRepository.getFollowings(Id, new Callback<List<FollowListResponse>>() {
            @Override
            public void onSuccess(List<FollowListResponse> result) {

                List<FollowUser> mapped = new ArrayList<>();

                for (FollowListResponse r : result) {
                    mapped.add(new FollowUser(
                            r.getName(),
                            r.getProfileImage(),
                            "FOLLOWING"
                    ));
                }

                adapter.updateList(mapped);
            }

            @Override
            public void onError(Exception e) {
                Log.e("FollowFragment", "팔로잉 불러오기 실패: " + e.getMessage());
            }
        });
    }
}
