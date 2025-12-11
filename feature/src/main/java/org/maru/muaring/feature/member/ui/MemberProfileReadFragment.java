package org.maru.muaring.feature.member.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
    private View privateLayout;
    private LinearLayout historySection;

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

        Bundle args = getArguments();
        if (args != null && args.containsKey("memberId")) {
            memberId = args.getLong("memberId");
        } else {
            memberId = -1L;
        }

        bindViews(view);

        setupHistoryRecycler(view);

        // TODO: ViewModel에서 실제 히스토리 데이터 받아오면 여기서 observe 해서 setItems 호출
//        historyAdapter.setItems(createDummyHistory());
        observeProfile();
        observeHistory();

        // 3) API 호출
        if (memberId != -1L) {
            viewModel.loadMemberProfile(memberId);
            viewModel.loadMemberHistory(memberId, null, null, 0);
        }
    }

    private void bindViews(View v) {
        profileImage = v.findViewById(R.id.image_profile);
        profileName = v.findViewById(R.id.text_profile_name);
        privateLayout = v.findViewById(R.id.layout_private_account);
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

        if (!profile.isMe() && !profile.isPublic() && !profile.isFollowing()) {
            privateLayout.setVisibility(View.VISIBLE);
            historySection.setVisibility(View.GONE);
            return;
        }

        privateLayout.setVisibility(View.GONE);
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
                    break;

                case SUCCESS:
                    if (res.data != null) {
                        historyAdapter.setItems(res.data);
                    }
                    break;

                case ERROR:
                    // 에러 UI
                    break;
            }
        });
    }
}



//    private void readMemberProfile() {
//
//    }

//    private List<MusicHistoryItem> createDummyHistory() {
//        List<MusicHistoryItem> list = new ArrayList<>();
//
//        list.add(new MusicHistoryItem(
//                2,
//                1L,
//                101L,
//                "영원은 그렇듯",
//                "리도어 (Redoor)",
//                "https://example.com/image1.jpg"
//        ));
//
//        list.add(new MusicHistoryItem(
//                3,
//                2L,
//                102L,
//                "네모네모",
//                "최예나",
//                "https://example.com/image2.jpg"
//        ));
//
//        list.add(new MusicHistoryItem(
//                11,
//                3L,
//                103L,
//                "IRIS OUT",
//                "Kenshi Yonezu",
//                "https://example.com/image3.jpg"
//        ));
//
//        return list;
//    }
//}
