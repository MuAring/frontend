package org.maru.muaring;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import org.maru.muaring.feature.common.navigation.CommonNavigator;
import org.maru.muaring.feature.common.navigation.LoginNavigator;
import org.maru.muaring.feature.auth.ui.LoginFragment;
import org.maru.muaring.feature.auth.ui.LoginViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity implements CommonNavigator, LoginNavigator {

    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.login_container, new LoginFragment())
                    .commit();
        }
    }

    @Override
    public void navigateToProfileSetup() {
        Intent intent = new Intent(this, ProfileSetupActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleSpotifyRedirect(intent);
    }

    private void handleSpotifyRedirect(Intent intent) {
        Uri uri = intent.getData();

        if (uri != null && uri.toString().startsWith(BuildConfig.SPOTIFY_REDIRECT_URI)) {

            String code = uri.getQueryParameter("code");

            if (code != null) {
                loginViewModel.loginWithSpotifyCode(code, this);
            } else {
                String error = uri.getQueryParameter("error");
                Toast.makeText(this, "Spotify 로그인 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void navigateToMain(@Nullable String nickname) {
        Intent intent = new Intent(this, MainActivity.class);
        if (nickname != null) {
            intent.putExtra("nickname", nickname);
        }
        startActivity(intent);
        finish();
    }
}
