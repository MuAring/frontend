package org.maru.muaring.feature.search.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.maru.muaring.core.ui.SegmentedToggleView;
import org.maru.muaring.feature.R;

public class SearchFragment extends Fragment {

    private SegmentedToggleView segmentedToggleView;
    private ImageButton btnBack;
    private TextView textTitle;
    private TextInputLayout textInputLayoutSearch;
    private TextInputEditText editSearch;
    private RecyclerView recyclerSearchResult;

    // 검색 어댑터 (나중에 구현)
    // private SearchAdapter searchAdapter;

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

        initViews(view);
        setupInitialState();
        setupListeners();
        setupRecyclerView();
    }

    private void initViews(View view) {
        segmentedToggleView = view.findViewById(R.id.segmentedToggle);
        btnBack = view.findViewById(R.id.btn_back);
        textTitle = view.findViewById(R.id.text_title);
        textInputLayoutSearch = view.findViewById(R.id.textInputLayoutSearch);
        editSearch = view.findViewById(R.id.editSearch);
        recyclerSearchResult = view.findViewById(R.id.recyclerSearchResult);
    }

    private void setupInitialState() {
        // 초기 상태: "사용자" 선택으로 맞추기 (리스너 설정 전!)
        segmentedToggleView.selectUser();
        updateUIForUserSearch();
    }

    private void setupListeners() {
        // 뒤로가기
        btnBack.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(SearchFragment.this);
            navController.popBackStack();
        });

        // 토글 리스너
        segmentedToggleView.setOnSegmentSelectedListener(new SegmentedToggleView.OnSegmentSelectedListener() {
            @Override
            public void onGroupSelected() {
                updateUIForGroupSearch();
                clearSearchResults();
            }

            @Override
            public void onUserSelected() {
                updateUIForUserSearch();
                clearSearchResults();
            }
        });

        // 검색어 입력 리스너
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupRecyclerView() {
        if (recyclerSearchResult != null) {
            recyclerSearchResult.setLayoutManager(new LinearLayoutManager(requireContext()));
            // TODO: 어댑터 설정
            // searchAdapter = new SearchAdapter();
            // recyclerSearchResult.setAdapter(searchAdapter);
        }
    }

    private void updateUIForGroupSearch() {
        textTitle.setText("어떤 그룹을 찾아볼까요?");
        textInputLayoutSearch.setHint("그룹 검색");
        editSearch.setText("");
    }

    private void updateUIForUserSearch() {
        textTitle.setText("어떤 사용자를 찾아볼까요?");
        textInputLayoutSearch.setHint("사용자 검색");
        editSearch.setText("");
    }

    private void clearSearchResults() {
        // TODO: RecyclerView 데이터 클리어
        // searchAdapter.clearData();
    }

    private void performSearch(String query) {
        if (query.trim().isEmpty()) {
            clearSearchResults();
            return;
        }

        if (segmentedToggleView.isGroupSelected()) {
            searchGroups(query);
        } else {
            searchUsers(query);
        }
    }

    private void searchGroups(String query) {
        // TODO: 그룹 검색 API 호출 및 결과 표시
        // 예시:
        // searchViewModel.searchGroups(query).observe(getViewLifecycleOwner(), results -> {
        //     searchAdapter.updateData(results);
        // });
    }

    private void searchUsers(String query) {
        // TODO: 사용자 검색 API 호출 및 결과 표시
        // 예시:
        // searchViewModel.searchUsers(query).observe(getViewLifecycleOwner(), results -> {
        //     searchAdapter.updateData(results);
        // });
    }
}