package org.maru.muaring;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.maru.muaring.feature.group.ui.GroupMemberFragment;
import org.maru.muaring.feature.search.ui.SearchNavigator;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements SearchNavigator {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String nickname = getIntent().getStringExtra("nickname");

        if (nickname != null) {
            Toast.makeText(this, nickname + "님 반가워요 🎉", Toast.LENGTH_SHORT).show();
        }

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

    // SearchNavigator 구현: 여기서만 nav_main.xml 리소스 사용
    @Override
    public void openGroupProfile(long groupId) {
        NavHostFragment host = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host);

        if (host == null) return;

        NavController nav = host.getNavController();

        Bundle args = new Bundle();
        args.putLong("groupId", groupId);

//        nav.navigate(R.id.groupProfileFragment, args);

        // R.id.groupProfileFragment 대신 이름으로 id 가져오기
        int destId = getResources().getIdentifier(
                "groupProfileFragment",   // nav_main.xml에서 쓴 id 이름
                "id",
                getPackageName()
        );

        if (destId == 0) {
            // 혹시라도 못 찾으면 토스트만 띄우고 리턴
            Toast.makeText(this, "groupProfileFragment id 를 찾을 수 없어요 🥲", Toast.LENGTH_SHORT).show();
            return;
        }

        nav.navigate(destId, args);
    }
}