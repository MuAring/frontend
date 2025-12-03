package org.maru.muaring;

import android.content.Intent;
import android.os.Bundle;
import org.maru.muaring.core.TokenManager;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.kakao.sdk.common.util.Utility;
import dagger.hilt.android.AndroidEntryPoint;
import jakarta.inject.Inject;

// ✨ 앱 처음 열릴 때. 로그인 여부에 따라 적절한 activity로 전환
@AndroidEntryPoint
public class SplashActivity extends AppCompatActivity {

    @Inject
    TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        String keyHash = Utility.INSTANCE.getKeyHash(this);
//        Log.d("KAKAO_KEY_HASH", "keyHash = " + keyHash);

        // 앱 시작 시 토큰 삭제 후 시작 (임시)
        TokenManager.clear(this);

        try {
            String token = tokenManager.getAccessToken();

            if (token == null) {
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                Log.d("ACESS_TOKEN", "token 있어요 ㅜㅜ: " + token);
                startActivity(new Intent(this, MainActivity.class));
            }

        } catch (Exception e) {
            // 예외 발생해도 로그인 화면으로
            startActivity(new Intent(this, LoginActivity.class));
        }

        finish(); // SplashActivity 종료
    }
}