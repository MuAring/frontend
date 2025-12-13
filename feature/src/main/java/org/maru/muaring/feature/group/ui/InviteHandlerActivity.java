package org.maru.muaring.feature.group.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import dagger.hilt.android.AndroidEntryPoint;
import org.maru.muaring.feature.R;

@AndroidEntryPoint
public class InviteHandlerActivity extends AppCompatActivity {

    private static final String TAG = "InviteHandler";
    private InviteViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(InviteViewModel.class);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        Uri data = intent.getData();

        Log.d(TAG, "받은 Intent URI: " + data);

        if (data == null) {
            showErrorAndFinish("잘못된 초대 링크입니다.");
            return;
        }

        // muaring://invite/{token} 형태 확인
        if (!"muaring".equals(data.getScheme()) || !"invite".equals(data.getHost())) {
            showErrorAndFinish("잘못된 초대 링크 형식입니다.");
            return;
        }

        String token = data.getLastPathSegment();

        if (token == null || token.isEmpty()) {
            showErrorAndFinish("초대 토큰이 없습니다.");
            return;
        }

        Log.d(TAG, "추출된 토큰: " + token);

        // 초대 정보 미리보기 로드
        loadInvitePreview(token);
    }

    private void loadInvitePreview(String token) {
        viewModel.getInvitePreview(token).observe(this, resource -> {
            switch (resource.status) {
                case LOADING:
                    // 로딩 상태 (선택사항)
                    break;

                case SUCCESS:
                    if (resource.data != null) {
                        navigateToPreview(token, resource.data);
                    } else {
                        showErrorAndFinish("초대 정보를 불러올 수 없습니다.");
                    }
                    break;

                case ERROR:
                    showErrorAndFinish(resource.message != null ?
                            resource.message : "초대 링크를 처리할 수 없습니다.");
                    break;
            }
        });
    }

    private void navigateToPreview(String token, InvitePreviewData data) {
        Intent intent = InvitePreviewActivity.newIntent(
                this,
                token,
                data.getGroupName(),
                data.getMemberCount(),
                data.getMaxMembers(),
                data.getGroupImageUrl()
        );
        startActivity(intent);
        finish();
    }

    private void showErrorAndFinish(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }
}