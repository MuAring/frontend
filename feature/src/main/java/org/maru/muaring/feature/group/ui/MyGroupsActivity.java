package org.maru.muaring.feature.group.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.dto.MyGroupSummary;
import org.maru.muaring.feature.R;
import org.maru.muaring.feature.group.ui.MyGroupsAdapter;
import org.maru.muaring.feature.group.ui.MyGroupsViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MyGroupsActivity extends AppCompatActivity {

    private MyGroupsViewModel viewModel;
    private MyGroupsAdapter adapter;
    private RecyclerView recyclerView;
    private EditText etSearch;
    private View progressBar;
    private View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);

        initToolbar();
        initViews();
        initViewModel();
        initRecyclerView();
        initSearchBar();
        observeData();

        // 초기 데이터 로드
        viewModel.loadMyGroups(null);
    }

    private void initToolbar() {
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("가입한 그룹");

        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_groups);
        progressBar = findViewById(R.id.progress_bar);
        emptyView = findViewById(R.id.empty_view);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(MyGroupsViewModel.class);
    }

    private void initRecyclerView() {
        adapter = new MyGroupsAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // 그룹 클릭 이벤트
        adapter.setOnGroupClickListener(group -> {
            // TODO: 그룹 상세 화면으로 이동
            Toast.makeText(this, group.getName() + " 클릭", Toast.LENGTH_SHORT).show();
        });
    }

    private void initSearchBar() {
        View searchBarLayout = findViewById(R.id.search_bar);
        etSearch = searchBarLayout.findViewById(R.id.etSearch);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                viewModel.searchGroups(query.isEmpty() ? null : query);
            }
        });
    }

    private void observeData() {
        viewModel.getGroups().observe(this, resource -> {
            if (resource == null) return;

            switch (resource.status) {
                case LOADING:
                    showLoading();
                    break;

                case SUCCESS:
                    hideLoading();
                    if (resource.data != null && !resource.data.isEmpty()) {
                        showGroups(resource.data);
                    } else {
                        showEmpty();
                    }
                    break;

                case ERROR:
                    hideLoading();
                    showError(resource.message);
                    break;
            }
        });
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    private void showGroups(java.util.List<MyGroupSummary> groups) {
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        adapter.setGroups(groups);
    }

    private void showEmpty() {
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    private void showError(String message) {
        Toast.makeText(this, message != null ? message : "오류가 발생했습니다",
                Toast.LENGTH_SHORT).show();
    }
}