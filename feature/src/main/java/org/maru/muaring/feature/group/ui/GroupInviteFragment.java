package org.maru.muaring.feature.group.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.maru.muaring.core.network.RetrofitClient;
import org.maru.muaring.data.model.ApiResponse;
import org.maru.muaring.data.model.GroupInviteResponse;
import org.maru.muaring.data.remote.api.GroupApi;
import org.maru.muaring.feature.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupInviteFragment extends Fragment {

    private static final String TAG = "GroupInviteFragment";

    private Button btnGoHome;
    private Button btnInviteFriend;
    private Button btnCopyLink;

    private GroupApi groupApi;
    private String inviteUrl;
    private Long groupId = 3L; // 임시로 일단 일케 해놓음 (나중에 Arguments로 받기)

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_invite, container, false);

        // API 인스턴스 생성
        groupApi = RetrofitClient.createService(GroupApi.class);

        // 뷰 초기화
        initViews(view);

        // 클릭 리스너 설정
        setupClickListeners();

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
        });

        // 친구 초대하기 버튼
        btnInviteFriend.setOnClickListener(v -> {
            createAndShareInviteLink();
        });

        // 초대 링크 복사 버튼
        btnCopyLink.setOnClickListener(v -> {
            if (inviteUrl != null) {
                copyToClipboard(inviteUrl);
            } else {
                createInviteLink(true); // 생성 후 복사
            }
        });
    }

    // 초대 링크 생성 및 공유
    private void createAndShareInviteLink() {
        createInviteLink(false); // 생성 후 공유
    }

    // 초대 링크 생성 API 호출
    private void createInviteLink(boolean shouldCopy) {
        Log.d(TAG, "초대 링크 생성 시작 - groupId: " + groupId);

        groupApi.createInviteLink(groupId).enqueue(new Callback<ApiResponse<GroupInviteResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<GroupInviteResponse>> call,
                                   Response<ApiResponse<GroupInviteResponse>> response) {

                Log.d(TAG, "응답 코드: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<GroupInviteResponse> apiResponse = response.body();
                    GroupInviteResponse data = apiResponse.getData();

                    if (data != null) {
                        inviteUrl = data.getInviteUrl();
                        Log.d(TAG, "초대 링크 생성 성공: " + inviteUrl);

                        Toast.makeText(getContext(),
                                "초대 링크가 생성되었습니다!",
                                Toast.LENGTH_SHORT).show();

                        // 복사 또는 공유
                        if (shouldCopy) {
                            copyToClipboard(inviteUrl);
                        } else {
                            shareInviteLink(inviteUrl);
                        }
                    } else {
                        Log.e(TAG, "데이터가 null입니다");
                        showError("데이터를 받아오지 못했습니다");
                    }
                } else {
                    Log.e(TAG, "응답 실패: " + response.message());

                    // 401 에러 (인증 필요)
                    if (response.code() == 401) {
                        showError("로그인이 필요합니다");
                    }
                    // 404 에러 (그룹 없음)
                    else if (response.code() == 404) {
                        showError("그룹을 찾을 수 없습니다");
                    }
                    // 기타 에러
                    else {
                        showError("초대 링크 생성 실패 (코드: " + response.code() + ")");
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<GroupInviteResponse>> call, Throwable t) {
                Log.e(TAG, "네트워크 오류", t);
                showError("네트워크 오류: " + t.getMessage());
            }
        });
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

    // 에러 메시지 표시
    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}