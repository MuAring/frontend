package org.maru.muaring;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment host = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host);

        if (host == null) {
            throw new IllegalStateException("NavHostFragment를 찾을 수 없습니다.");
            // 필요하면 여기서 그냥 return; 으로 안전하게 처리해도 됨
        }

        NavController nav = host.getNavController();

        BottomNavigationView bottom = findViewById(R.id.bottomNav);
        NavigationUI.setupWithNavController(bottom, nav);
        // 이걸로 bottomNav <-> nav_main.xml 연결 전부 해줌
    }
}