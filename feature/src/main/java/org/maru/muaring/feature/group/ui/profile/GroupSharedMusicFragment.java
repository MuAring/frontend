package org.maru.muaring.feature.group.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.maru.muaring.feature.R;
import org.maru.muaring.feature.today.ui.TodayPostsFragment;

public class GroupSharedMusicFragment extends Fragment {

    private static final String ARG_GROUP_ID = "arg_group_id";
    private Long groupId;

    public static GroupSharedMusicFragment newInstance(long groupId) {
        GroupSharedMusicFragment f = new GroupSharedMusicFragment();
        Bundle b = new Bundle();
        b.putLong(ARG_GROUP_ID, groupId);
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_shared_music, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            long gid = getArguments().getLong(ARG_GROUP_ID, -1L);
            groupId = (gid > 0) ? gid : null;
        }

        // ===== 툴바 세팅 =====
        View toolbar = view.findViewById(R.id.include_toolbar);
        TextView title = toolbar.findViewById(org.maru.muaring.core.R.id.toolbar_title);
        ImageButton back = toolbar.findViewById(R.id.btn_back);

        if (title != null) title.setText("공유한 음악");
        if (back != null) back.setOnClickListener(v -> requireActivity().onBackPressed());

        // ===== TodayPostsFragment를 child로 붙이기 =====
        if (savedInstanceState == null) {
            TodayPostsFragment child = TodayPostsFragment.newInstanceForGroupProfile(groupId);

            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_today_posts, child)
                    .commit();
        }
    }
}
