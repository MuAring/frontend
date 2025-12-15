package org.maru.muaring.feature.group.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.maru.muaring.core.TokenManager;
import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.dto.MyGroupSummary;
import org.maru.muaring.feature.R;

import dagger.hilt.android.AndroidEntryPoint;
import jakarta.inject.Inject;

@AndroidEntryPoint
public class MyGroupsFragment extends Fragment {

    private MyGroupsViewModel viewModel;
    private MyGroupsAdapter adapter;
    private RecyclerView recyclerView;
    private EditText etSearch;
    private View progressBar;
    private View emptyView;
    private Long targetMemberId; // 조회할 멤버 ID

    @Inject
    TokenManager tokenManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_groups, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // arguments에서 memberId 가져오기
        if (getArguments() != null && getArguments().containsKey("memberId")) {
            long argMemberId = getArguments().getLong("memberId", -1L);
            if (argMemberId > 0) {
                targetMemberId = argMemberId;
            }
        }

        // targetMemberId가 여전히 null이면 현재 로그인한 사용자 ID 사용
        if (targetMemberId == null || targetMemberId <= 0) {
            targetMemberId = tokenManager.getMemberId();
        }

        initToolbar(view);
        initViews(view);
        initViewModel();
        initRecyclerView();
        initSearchBar(view);
        observeData();

        // 초기 데이터 로드
        viewModel.loadMemberGroups(targetMemberId, null);
    }

    private void initToolbar(View view) {
        View toolbar = view.findViewById(R.id.toolbar);
        TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);

        // 본인 조회인지 다른 사용자 조회인지에 따라 제목 변경
        if (targetMemberId == null) {
            toolbarTitle.setText("가입한 그룹");
        } else {
            toolbarTitle.setText("가입한 그룹");
        }

        ImageButton btnBack = toolbar.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigateUp();
        });
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_groups);
        progressBar = view.findViewById(R.id.progress_bar);
        emptyView = view.findViewById(R.id.empty_view);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(MyGroupsViewModel.class);
    }

    private void initRecyclerView() {
        adapter = new MyGroupsAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // 그룹 클릭 이벤트 - 그룹 프로필로 이동
        adapter.setOnGroupClickListener(group -> {
            navigateToGroupProfile(group.getGroupId());
        });
    }

    private void navigateToGroupProfile(Long groupId) {
        if (groupId == null || groupId <= 0) {
            Toast.makeText(requireContext(), "그룹 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle args = new Bundle();
        args.putLong("groupId", groupId);

        try {
            NavController navController = NavHostFragment.findNavController(this);
            int actionId = getResources().getIdentifier(
                    "action_myGroups_to_groupProfile",
                    "id",
                    requireContext().getPackageName()
            );

            if (actionId != 0) {
                navController.navigate(actionId, args);
            } else {
                // action이 없으면 직접 destination으로 이동
                int destinationId = getResources().getIdentifier(
                        "groupProfileFragment",
                        "id",
                        requireContext().getPackageName()
                );
                if (destinationId == 0) {
                    throw new IllegalArgumentException("Destination ID not found: groupProfileFragment");
                }
                navController.navigate(destinationId, args);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "화면 전환에 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void initSearchBar(View view) {
        View searchBarLayout = view.findViewById(R.id.search_bar);
        etSearch = searchBarLayout.findViewById(R.id.etSearch);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                viewModel.searchGroups(targetMemberId, query.isEmpty() ? null : query);
            }
        });
    }

    private void observeData() {
        viewModel.getGroups().observe(getViewLifecycleOwner(), resource -> {
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
        Toast.makeText(requireContext(),
                message != null ? message : "오류가 발생했습니다",
                Toast.LENGTH_SHORT).show();
    }
}