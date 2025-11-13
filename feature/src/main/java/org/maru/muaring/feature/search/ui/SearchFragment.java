package org.maru.muaring.feature.search.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.maru.muaring.core.ui.SegmentedToggleView;
import org.maru.muaring.feature.R;

public class SearchFragment extends Fragment {

    private SegmentedToggleView segmentedToggleView;
    private ImageButton btnBack;
    private TextInputLayout textInputLayoutSearch;
    private TextInputEditText editSearch;
    private RecyclerView recyclerSearchResult;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        segmentedToggleView = view.findViewById(R.id.segmentedToggle);
        btnBack = view.findViewById(R.id.btn_back);
        textInputLayoutSearch = view.findViewById(R.id.textInputLayoutSearch);
        editSearch = view.findViewById(R.id.editSearch);
//        recyclerSearchResult = view.findViewById(R.id.recyclerSearchResult);

        // 초기 상태: "사용자" 선택으로 맞추기 (리스너 설정 전!)
        segmentedToggleView.selectUser();

        // 뒤로가기
        btnBack.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(SearchFragment.this);
            navController.popBackStack();
        });

        // 토글 리스너
        segmentedToggleView.setOnSegmentSelectedListener(new SegmentedToggleView.OnSegmentSelectedListener() {
            @Override
            public void onGroupSelected() {
                // 그룹 탭으로 이동 (네비게이션 그래프 액션 ID 에 맞게 수정)
                NavController navController = NavHostFragment.findNavController(SearchFragment.this);
                // 예시: navController.navigate(R.id.action_searchFragment_to_groupSearchFragment);
                // 실제 액션 id 로 바꿔줘야 함
            }

            @Override
            public void onUserSelected() {
                // 지금이 "사용자" 화면이라면 아무 동작 안 해도 됨
            }
        });

        // TODO: editSearch 텍스트 변경 리스너, 검색 실행, RecyclerView 어댑터 설정 등 구현
    }
}
