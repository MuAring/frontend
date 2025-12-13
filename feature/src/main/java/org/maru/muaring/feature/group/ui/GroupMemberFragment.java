package org.maru.muaring.feature.group.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import org.maru.muaring.core.ui.CommonToolbarView;
import org.maru.muaring.data.api.dto.GroupMemberResponse;
import org.maru.muaring.feature.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GroupMemberFragment extends Fragment {

    private static final String ARG_GROUP_ID = "group_id";

    private GroupMemberViewModel viewModel;
    private GroupMemberAdapter adapter;

    private CommonToolbarView toolbar;
    private EditText etSearch;
    private RecyclerView recyclerMembers;
    private ProgressBar progressBar;
    private TextView textEmpty;

    private Long groupId;
    private String currentSearch = "";

    public static GroupMemberFragment newInstance(Long groupId) {
        GroupMemberFragment fragment = new GroupMemberFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_GROUP_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            groupId = getArguments().getLong(ARG_GROUP_ID);
        }

        viewModel = new ViewModelProvider(this).get(GroupMemberViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_member, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupToolbar();
        setupRecyclerView();
        setupSearchBar();
        observeViewModel();

        // 초기 멤버 목록 로드
        loadMembers();
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        recyclerMembers = view.findViewById(R.id.recyclerMembers);
        progressBar = view.findViewById(R.id.progressBar);
        textEmpty = view.findViewById(R.id.textEmpty);

        // include된 레이아웃에서 EditText 찾기
        etSearch = view.findViewById(R.id.etSearch);
    }

    private void setupToolbar() {
        toolbar.setTitle("그룹 멤버");
    }

    private void setupRecyclerView() {
        adapter = new GroupMemberAdapter();

        adapter.setOnMemberClickListener(member -> {
            navigateToMemberProfile(member);
        });

        recyclerMembers.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerMembers.setAdapter(adapter);
    }

    // 각 그룹 멤버 프로필 조회하러 이동
    private void navigateToMemberProfile(GroupMemberResponse member) {
        // 전달할 데이터
        Bundle args = new Bundle();
        args.putLong("memberId", member.getMemberId());

        try {
            NavController navController = NavHostFragment.findNavController(this);
            // 리소스 이름으로 action ID 가져오기
            int actionId = getResources().getIdentifier(
                    "action_groupMember_to_memberProfile",
                    "id",
                    requireContext().getPackageName()
            );
            // 액션 ID 유효성 체크
            if (actionId == 0) {
                throw new IllegalArgumentException("Action ID not found: action_groupMember_to_memberProfile");
            }
            navController.navigate(actionId, args);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(),
                    "화면 전환 실패: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSearchBar() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearch = s.toString();
                loadMembers();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void observeViewModel() {
        // 멤버 목록 관찰
        viewModel.members.observe(getViewLifecycleOwner(), members -> {
            if (members != null && !members.isEmpty()) {
                adapter.setMembers(members);
                recyclerMembers.setVisibility(View.VISIBLE);
                textEmpty.setVisibility(View.GONE);
            } else {
                recyclerMembers.setVisibility(View.GONE);
                textEmpty.setVisibility(View.VISIBLE);
            }
        });

        // 로딩 상태 관찰
        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                recyclerMembers.setVisibility(View.GONE);
                textEmpty.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        });

        // 에러 메시지 관찰
        viewModel.errorMessage.observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMembers() {
        if (groupId != null) {
            String search = currentSearch.isEmpty() ? null : currentSearch;
            viewModel.loadGroupMembers(groupId, search);
        }
    }
}