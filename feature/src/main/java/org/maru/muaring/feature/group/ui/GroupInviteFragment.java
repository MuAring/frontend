package org.maru.muaring.feature.group.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.maru.muaring.data.api.dto.GroupInviteResponse;
import org.maru.muaring.feature.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GroupInviteFragment extends Fragment {

    private static final String TAG = "GroupInviteFragment";
    private static final String ARG_GROUP_ID = "group_id";

    private Button btnGoHome;
    private Button btnInviteFriend;
    private Button btnCopyLink;

    private GroupInviteViewModel viewModel;
    private String inviteUrl;
    private Long groupId;

    public static GroupInviteFragment newInstance(Long groupId) {
        GroupInviteFragment fragment = new GroupInviteFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_GROUP_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Arguments에서 groupId 가져오기
        if (getArguments() != null) {
            groupId = getArguments().getLong(ARG_GROUP_ID);
        } else {
            groupId = 3L; // 임시 기본값
        }

        // ViewModel 초기화
        viewModel = new ViewModelProvider(this).get(GroupInviteViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_group_creation_complete, container, false);

        // 뷰 초기화
        initViews(view);

        // 클릭 리스너 설정
        setupClickListeners();

        // ViewModel observe
        observeViewModel();

        return view;
    }

    private void initViews(View view) {
        btnGoHome = view.findViewById(R.id.btnGoHome);
        btnInviteFriend = view.findViewById(R.id.btnInviteFriend);
        btnCopyLink = view.findViewById(R.id.btnCopyLink);
    }

    private void setupClickListeners() {
        // 홈으로 버튼
        btnGoHome.setOnClickListener(v -> {
            Toast.makeText(getContext(), "홈으로 이동", Toast.LENGTH_SHORT).show();
            // Navigation으로 홈 화면 이동
            // NavHostFragment.findNavController(this).navigate(R.id.home);
        });

        // 친구 초대하기 버튼
        btnInviteFriend.setOnClickListener(v -> {
            if (inviteUrl != null) {
                // 이미 링크가 있으면 바로 공유
                shareInviteLink(inviteUrl);
            } else {
                // 링크가 없으면 생성 후 공유
                viewModel.createInviteLinkForShare(groupId);
            }
        });

        // 초대 링크 복사 버튼
        btnCopyLink.setOnClickListener(v -> {
            if (inviteUrl != null) {
                // 이미 링크가 있으면 바로 복사
                copyToClipboard(inviteUrl);
            } else {
                // 링크가 없으면 생성 후 복사
                viewModel.createInviteLinkForCopy(groupId);
            }
        });
    }

    private void observeViewModel() {
        // 초대 링크 생성 결과
        viewModel.getInviteLinkResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        // 로딩 상태 - 버튼 비활성화
                        setButtonsEnabled(false);
                        break;

                    case SUCCESS:
                        // 성공 - 버튼 활성화 및 결과 처리
                        setButtonsEnabled(true);
                        handleSuccess(resource.data);
                        break;

                    case ERROR:
                        // 에러 - 버튼 활성화 및 에러 표시
                        setButtonsEnabled(true);
                        handleError(resource.message);
                        break;
                }
            }
        });
    }

    private void handleSuccess(GroupInviteResponse data) {
        if (data != null && data.getInviteUrl() != null) {
            inviteUrl = data.getInviteUrl();

            Toast.makeText(getContext(),
                    "초대 링크가 생성되었습니다!",
                    Toast.LENGTH_SHORT).show();

            // Pending Action에 따라 복사 또는 공유 수행
            GroupInviteViewModel.InviteAction action = viewModel.getPendingAction().getValue();
            if (action != null) {
                switch (action) {
                    case COPY:
                        copyToClipboard(inviteUrl);
                        break;
                    case SHARE:
                        shareInviteLink(inviteUrl);
                        break;
                }
                viewModel.clearPendingAction();
            }
        }
    }

    private void handleError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        viewModel.clearPendingAction();
    }

    private void setButtonsEnabled(boolean enabled) {
        btnInviteFriend.setEnabled(enabled);
        btnCopyLink.setEnabled(enabled);
    }

    // 초대 링크 공유하기
    private void shareInviteLink(String url) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "뮤어링 그룹에 초대합니다! 함께 음악을 공유해요 🎵\n\n" + url);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "뮤어링 그룹 초대");

        startActivity(Intent.createChooser(shareIntent, "친구 초대하기"));
    }

    // 클립보드에 복사
    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager)
                requireContext().getSystemService(Context.CLIPBOARD_SERVICE);

        ClipData clip = ClipData.newPlainText("뮤어링 초대 링크", text);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(getContext(),
                "초대 링크가 복사되었습니다! 📋",
                Toast.LENGTH_SHORT).show();
    }
}