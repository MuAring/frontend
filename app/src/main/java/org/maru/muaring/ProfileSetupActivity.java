package org.maru.muaring;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.maru.muaring.feature.common.navigation.CommonNavigator;
import org.maru.muaring.feature.member.ui.ProfileSetupFragment;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileSetupActivity extends AppCompatActivity implements CommonNavigator {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.profile_setup_container, new ProfileSetupFragment())
                    .commit();
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