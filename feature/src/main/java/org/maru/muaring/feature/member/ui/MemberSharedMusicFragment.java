package org.maru.muaring.feature.member.ui;

import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.maru.muaring.feature.R;
import org.maru.muaring.feature.today.ui.TodayPostsFragment;

public class MemberSharedMusicFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_member_shared_music, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        // ===== 툴바 세팅 =====
        View toolbar = view.findViewById(R.id.include_toolbar);
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        ImageButton back = toolbar.findViewById(R.id.btn_back);

        title.setText("공유한 음악");
        back.setOnClickListener(v -> requireActivity().onBackPressed());

        // 멤버 프로필 전용: 다른 xml + 제목 숨김 + /post/me/today
        if (savedInstanceState == null) {
            TodayPostsFragment child = TodayPostsFragment
                    .newInstanceForProfile(false, true);

            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_today_posts, child)
                    .commit();
        }
    }
}
