package org.maru.muaring;

import android.content.Intent;
import android.os.Bundle;
import org.maru.muaring.core.TokenManager;

import androidx.appcompat.app.AppCompatActivity;

import dagger.hilt.android.AndroidEntryPoint;

// ✨ 앱 처음 열릴 때. 로그인 여부에 따라 적절한 activity로 전환
@AndroidEntryPoint
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            String token = TokenManager.getAccessToken(this);

            if (token == null) {
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                startActivity(new Intent(this, MainActivity.class));
            }

        } catch (Exception e) {
            // 예외 발생해도 로그인 화면으로
            startActivity(new Intent(this, LoginActivity.class));
        }

        finish(); // SplashActivity 종료
    }
}

