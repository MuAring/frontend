package org.maru.muaring.core.ui;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import org.maru.muaring.core.R;

public class CommonToolbarView extends RelativeLayout {

    public CommonToolbarView(Context context) {
        super(context);
        init();
    }

    public CommonToolbarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CommonToolbarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.toolbar_common, this, true);

        ImageButton btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> {
            Activity activity = findActivity(getContext());
            if (activity == null) {
                return;
            }

            // Navigation Component 뒤로가기 시도
            if (activity instanceof FragmentActivity fa) {
                // NavHostFragment 찾기
                // 리소스 이름으로 ID 찾기
                int navHostId = activity.getResources().getIdentifier(
                        "nav_host",
                        "id",
                        activity.getPackageName()
                );

                Fragment navHost = fa.getSupportFragmentManager()
                        .findFragmentById(navHostId);

                if (navHost instanceof NavHostFragment) {
                    NavController navController = ((NavHostFragment) navHost).getNavController();
                    // Navigation 백스택이 있으면 뒤로가기
                    if (navController.getCurrentDestination() != null
                            && navController.getPreviousBackStackEntry() != null) {
                        navController.popBackStack();
                        return;
                    }
                }

                // Navigation이 없거나 백스택이 비었으면 FragmentManager 확인
                if (fa.getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    fa.getSupportFragmentManager().popBackStack();
                    return;
                }
            }

            // 없으면 Activity 종료
            activity.finish();
        });

        // 상태바 높이만큼 padding 적용
        ViewCompat.setOnApplyWindowInsetsListener(this, (v, insets) -> {
            int topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            v.setPadding(0, topInset, 0, 0);

            return insets;
        });

    }

    private Activity findActivity(Context context) {
        if (context instanceof Activity) return (Activity) context;
        if (context instanceof ContextWrapper) {
            Context base = ((ContextWrapper) context).getBaseContext();
            if (base instanceof Activity) return (Activity) base;
        }
        return null;
    }

    // 툴바 제목 변경
    public void setTitle(String title) {
        android.widget.TextView tv = findViewById(R.id.toolbar_title);
        if (tv != null) tv.setText(title);
    }
}