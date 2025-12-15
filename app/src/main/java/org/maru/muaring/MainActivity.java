package org.maru.muaring;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.maru.muaring.core.TokenManager;
import org.maru.muaring.core.common.CommentInputController;
import org.maru.muaring.feature.nearby.ui.LocationManager;
import org.maru.muaring.feature.search.ui.SearchNavigator;
import dagger.hilt.android.AndroidEntryPoint;
import jakarta.inject.Inject;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements SearchNavigator, CommentInputController {

    @Inject
    TokenManager tokenManager;

    @Inject
    LocationManager locationManager;

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

        bottom.setOnItemSelectedListener(item -> {
            return NavigationUI.onNavDestinationSelected(item, nav);
        });

        ProcessLifecycleOwner.get()
                .getLifecycle()
                .addObserver(new AppLifecycleObserver());

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
            Toast.makeText(this,
                    "해당 그룹을 찾을 수 없어요 🥲",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        nav.navigate(destId, args);
    }

    @Override
    public void openMemberProfile(long memberId) {
        NavHostFragment host = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host);

        if (host == null) return;

        NavController nav = host.getNavController();

        Bundle args = new Bundle();
        args.putLong("memberId", memberId);

        int destId = getResources().getIdentifier(
                "memberProfileFragment",
                "id",
                getPackageName()
        );

        if (destId == 0) {
            Toast.makeText(this,
                    "해당 멤버를 찾을 수 없어요 🥲",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        nav.navigate(destId, args);
    }

    @Override
    public void navigateToProfileEdit() {
        NavHostFragment host = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host);

        if (host == null) return;

        NavController nav = host.getNavController();

        int destId = getResources().getIdentifier(
                "profileEditFragment",
                "id",
                getPackageName()
        );

        if (destId == 0) {
            Toast.makeText(this, "profileEditFragment id 를 찾을 수 없어요 🥲", Toast.LENGTH_SHORT).show();
            return;
        }

        nav.navigate(destId);
    }

    public void openMyProfile() {
        long myId = tokenManager.getMemberId();
        if (myId == -1L) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // NavHost 가져오기
        NavHostFragment host = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host);

        if (host == null) return;

        NavController nav = host.getNavController();

        // Argument 구성
        Bundle args = new Bundle();
        args.putLong("memberId", myId);

        int destId = getResources().getIdentifier(
                "profileFragment",  // xml에 정의된 id
                "id",
                getPackageName()
        );

        if (destId == 0) {
            Toast.makeText(this, "memberProfileReadFragment id 를 찾을 수 없어요 🥲", Toast.LENGTH_SHORT).show();
            return;
        }

        nav.navigate(destId, args);
    }

    @Override
    public void navigateToPostDetail(long postId) {

        // NavHostFragment 가져오기
        NavHostFragment host = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host);

        if (host == null) {
            return;
        }

        NavController nav = host.getNavController();

        // Bundle로 postId 전달
        Bundle args = new Bundle();
        args.putLong("postId", postId);

        // destination id로 이동
        nav.navigate(org.maru.muaring.R.id.postDetailFragment, args);
    }

    // PostDetailFragment에서만 댓글 입력창 보이도록
    @Override
    public void setCommentInputVisible(boolean visible) {
        View commentInput = findViewById(R.id.comment_input);
        if (commentInput != null) {
            commentInput.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    public class AppLifecycleObserver implements LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        public void onEnterForeground() {
            locationManager.start();
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        public void onEnterBackground() {
            locationManager.stop();
        }
    }
}