package org.maru.muaring.feature.search.ui.group;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.maru.muaring.core.ui.SegmentedToggleView;
import org.maru.muaring.feature.R;

public class GroupSearchFragment extends Fragment {

    private SegmentedToggleView segmentedToggle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        segmentedToggle = view.findViewById(R.id.segmentedToggle);

        segmentedToggle.setOnSegmentSelectedListener(new SegmentedToggleView.OnSegmentSelectedListener() {
            @Override
            public void onGroupSelected() {
                // 현재 화면이 그룹이라면 아무 것도 안 해도 되고,
                // 사용자 탭에서 들어왔다면 여기서 그룹 화면으로 전환하는 처리
            }

            @Override
            public void onUserSelected() {
                // 네비게이션으로 UserSearchFragment로 이동
                // NavController nav = Navigation.findNavController(view);
                // nav.navigate(R.id.action_groupSearch_to_userSearch);
            }
        });

        // 여기서 검색 로직, 뒤로가기 버튼 등 이어서 구현
    }
}

