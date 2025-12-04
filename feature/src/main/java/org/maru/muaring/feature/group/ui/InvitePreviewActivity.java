package org.maru.muaring.feature.group.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import dagger.hilt.android.AndroidEntryPoint;
import org.maru.muaring.feature.R;

@AndroidEntryPoint
public class InvitePreviewActivity extends AppCompatActivity {

    private static final String EXTRA_TOKEN = "token";
    private static final String EXTRA_GROUP_NAME = "group_name";
    private static final String EXTRA_MEMBER_COUNT = "member_count";
    private static final String EXTRA_MAX_MEMBERS = "max_members";
    private static final String EXTRA_GROUP_IMAGE = "group_image";

    private InviteViewModel viewModel;
    private String inviteToken;

    private ImageView ivGroupImage;
    private TextView tvGroupName;
    private TextView tvMemberCount;
    private Button btnJoin;
    private Button btnCancel;

    public static Intent newIntent(Context context, String token, String groupName,
                                   int memberCount, int maxMembers, String groupImageUrl) {
        Intent intent = new Intent(context, InvitePreviewActivity.class);
        intent.putExtra(EXTRA_TOKEN, token);
        intent.putExtra(EXTRA_GROUP_NAME, groupName);
        intent.putExtra(EXTRA_MEMBER_COUNT, memberCount);
        intent.putExtra(EXTRA_MAX_MEMBERS, maxMembers);
        intent.putExtra(EXTRA_GROUP_IMAGE, groupImageUrl);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_preview);

        viewModel = new ViewModelProvider(this).get(InviteViewModel.class);

        inviteToken = getIntent().getStringExtra(EXTRA_TOKEN);
        String groupName = getIntent().getStringExtra(EXTRA_GROUP_NAME);
        int memberCount = getIntent().getIntExtra(EXTRA_MEMBER_COUNT, 0);
        int maxMembers = getIntent().getIntExtra(EXTRA_MAX_MEMBERS, 0);
        String groupImageUrl = getIntent().getStringExtra(EXTRA_GROUP_IMAGE);

        initViews();
        displayGroupInfo(groupName, memberCount, maxMembers, groupImageUrl);
        setupClickListeners();
        observeViewModel();
    }

    private void initViews() {
        ivGroupImage = findViewById(R.id.iv_group_image);
        tvGroupName = findViewById(R.id.tv_group_name);
        tvMemberCount = findViewById(R.id.tv_member_count);
        btnJoin = findViewById(R.id.btn_join);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    private void displayGroupInfo(String groupName, int memberCount, int maxMembers, String groupImageUrl) {
        tvGroupName.setText(groupName);
        tvMemberCount.setText(memberCount + "/" + maxMembers + "명");

        if (groupImageUrl != null && !groupImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(groupImageUrl)
                    .placeholder(R.drawable.ic_group_default)
                    .into(ivGroupImage);
        }
    }

    private void setupClickListeners() {
        btnJoin.setOnClickListener(v -> joinGroup());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void observeViewModel() {
        viewModel.joinGroup(inviteToken).observe(this, resource -> {
            switch (resource.status) {
                case LOADING:
                    btnJoin.setEnabled(false);
                    btnJoin.setText("가입 중...");
                    break;

                case SUCCESS:
                    btnJoin.setEnabled(true);
                    btnJoin.setText("그룹 참여하기");
                    Toast.makeText(this, "그룹에 참여했습니다!", Toast.LENGTH_SHORT).show();
                    // TODO: 홈 화면 또는 그룹 상세 화면으로 이동
                    finish();
                    break;

                case ERROR:
                    btnJoin.setEnabled(true);
                    btnJoin.setText("그룹 참여하기");
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void joinGroup() {
        viewModel.joinGroup(inviteToken);
    }
}