package org.maru.muaring.feature.group.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import dagger.hilt.android.AndroidEntryPoint;

import org.maru.muaring.feature.R;

@AndroidEntryPoint
public class GroupInviteActivity extends AppCompatActivity {

    private static final String TAG = "GroupComplete";
    private static final String EXTRA_GROUP_ID = "group_id";
    private static final String EXTRA_HAS_PROFILE_IMAGE = "has_profile_image";
    private static final String EXTRA_PROFILE_BITMAP = "profile_bitmap";

    private ImageView ivProfileImage;
    private Button btnGoHome;
    private Button btnInviteFriend;
    private Button btnCopyLink;

    private GroupInviteViewModel viewModel;
    private Long groupId;

    public static Intent newIntent(Context context, Long groupId, boolean hasProfileImage, Bitmap profileBitmap) {
        Intent intent = new Intent(context, GroupInviteActivity.class);
        intent.putExtra(EXTRA_GROUP_ID, groupId);
        intent.putExtra(EXTRA_HAS_PROFILE_IMAGE, hasProfileImage);
        if (profileBitmap != null) {
            intent.putExtra(EXTRA_PROFILE_BITMAP, profileBitmap);
        }
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_invite);

        groupId = getIntent().getLongExtra(EXTRA_GROUP_ID, -1L);
        boolean hasProfileImage = getIntent().getBooleanExtra(EXTRA_HAS_PROFILE_IMAGE, false);
        Bitmap profileBitmap = getIntent().getParcelableExtra(EXTRA_PROFILE_BITMAP);

        if (groupId == -1L) {
            Toast.makeText(this, "그룹 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(GroupInviteViewModel.class);

        initViews();
        setupClickListeners();
        observeViewModel();

        // 업로드된 이미지가 있으면 표시, 없으면 기본 아이콘 유지
        if (hasProfileImage && profileBitmap != null) {
            ivProfileImage.setImageBitmap(profileBitmap);
            ivProfileImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ivProfileImage.setPadding(0, 0, 0, 0);
        }
        // else: 레이아웃에 이미 ic_group_default가 설정되어 있으므로 아무것도 안 함
    }

    private void initViews() {
        ivProfileImage = findViewById(R.id.iv_profile_image);
        btnGoHome = findViewById(R.id.btn_go_home);
        btnInviteFriend = findViewById(R.id.btn_invite_friend);
        btnCopyLink = findViewById(R.id.btn_copy_link);
    }

    private void setupClickListeners() {
        btnGoHome.setOnClickListener(v -> goToHome());

        btnInviteFriend.setOnClickListener(v -> {
            // TODO: 친구 초대 화면으로 이동
            Toast.makeText(this, "친구 초대 기능 준비 중", Toast.LENGTH_SHORT).show();
        });

        btnCopyLink.setOnClickListener(v -> generateAndCopyInviteLink());
    }

    private void observeViewModel() {
        viewModel.getInviteLinkStatus().observe(this, resource -> {
            switch (resource.status) {
                case LOADING:
                    btnCopyLink.setEnabled(false);
                    btnCopyLink.setText("링크 생성 중...");
                    break;

                case SUCCESS:
                    btnCopyLink.setEnabled(true);
                    btnCopyLink.setText("초대 링크 복사");
                    if (resource.data != null) {
                        copyToClipboard(resource.data);
                        Toast.makeText(this, "초대 링크가 복사되었습니다!", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case ERROR:
                    btnCopyLink.setEnabled(true);
                    btnCopyLink.setText("초대 링크 복사");
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void generateAndCopyInviteLink() {
        viewModel.createInviteLink(groupId);
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("초대 링크", text);
        clipboard.setPrimaryClip(clip);
    }

    private void goToHome() {
        // TODO: 홈 화면으로 이동 (실제 홈 Activity로 교체)
        Toast.makeText(this, "홈으로 이동", Toast.LENGTH_SHORT).show();
        finish();
    }
}