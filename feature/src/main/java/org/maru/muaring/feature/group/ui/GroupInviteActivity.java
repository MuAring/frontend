package org.maru.muaring.feature.group.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import dagger.hilt.android.AndroidEntryPoint;

import org.maru.muaring.core.util.Resource;
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
    private static final String EXTRA_PROFILE_URI = "profile_uri";
    public static Intent newIntent(Context context, Long groupId, boolean hasProfileImage, Uri imageUri) {
        Intent intent = new Intent(context, GroupInviteActivity.class);
        intent.putExtra(EXTRA_GROUP_ID, groupId);
        intent.putExtra(EXTRA_HAS_PROFILE_IMAGE, hasProfileImage);

        if (imageUri != null) {
            intent.putExtra(EXTRA_PROFILE_URI, imageUri.toString());
        }

        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_invite);

        groupId = getIntent().getLongExtra(EXTRA_GROUP_ID, -1L);
        boolean hasProfileImage = getIntent().getBooleanExtra(EXTRA_HAS_PROFILE_IMAGE, false);

        String uriString = getIntent().getStringExtra(EXTRA_PROFILE_URI);
        Uri imageUri = uriString != null ? Uri.parse(uriString) : null;

        if (groupId == -1L) {
            Toast.makeText(this, "그룹 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(GroupInviteViewModel.class);

        initViews();
        setupClickListeners();
        observeViewModel();

        if (hasProfileImage && imageUri != null) {
            ivProfileImage.setImageURI(imageUri);
            ivProfileImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ivProfileImage.setPadding(0, 0, 0, 0);
        }
    }


    private void initViews() {
        ivProfileImage = findViewById(R.id.iv_profile_image);
        btnGoHome = findViewById(R.id.btn_go_home);
        btnInviteFriend = findViewById(R.id.btn_invite_friend);
        btnCopyLink = findViewById(R.id.btn_copy_link);
    }

    private void setupClickListeners() {
        btnGoHome.setOnClickListener(v -> goToHome());

        btnInviteFriend.setOnClickListener(v -> shareInviteLink());

        btnCopyLink.setOnClickListener(v -> generateAndCopyInviteLink());
    }

    private void shareInviteLink() {
        // 초대 링크 생성 후 공유
        viewModel.createInviteLinkForSharing(groupId);
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

        // 공유용 초대 링크 관찰
        viewModel.getInviteLinkForSharing().observe(this, resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                openShareDialog(resource.data);
            } else if (resource.status == Resource.Status.ERROR) {
                Toast.makeText(this, "초대 링크 생성 실패: " + resource.message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void openShareDialog(String inviteUrl) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "뮤어링 그룹 초대");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "뮤어링 그룹에 초대합니다! 아래 링크를 클릭해서 참여하세요.\n\n" + inviteUrl);

        Intent chooser = Intent.createChooser(shareIntent, "친구 초대하기");
        startActivity(chooser);
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
        // TODO: 홈 화면으로 이동
        Toast.makeText(this, "홈으로 이동", Toast.LENGTH_SHORT).show();
        finish();
    }
}