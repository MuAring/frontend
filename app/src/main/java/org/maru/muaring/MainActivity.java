package org.maru.muaring;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment host = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host);

        if (host == null) {
            throw new IllegalStateException("NavHostFragment를 찾을 수 없습니다.");
            // 사용자 환경에서의 안전성 우선하려면 return; 으로 부드럽게 처리 O
        }

        NavController nav = host.getNavController();

        BottomNavigationView bottom = findViewById(R.id.bottomNav);
        NavigationUI.setupWithNavController(bottom, nav);

        FloatingActionButton fab = findViewById(R.id.btnUpload);
        fab.setOnClickListener(v -> nav.navigate(R.id.uploadFragment));
    }
}