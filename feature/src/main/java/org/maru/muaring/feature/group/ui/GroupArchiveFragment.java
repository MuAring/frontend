package org.maru.muaring.feature.group.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.maru.muaring.feature.R;
import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.dto.MusicArchiveDto;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GroupArchiveFragment extends Fragment {

    private static final String TAG = "GroupArchiveFragment";
    private static final String ARG_GROUP_ID = "groupId";

    private Long groupId;
    private GroupArchiveViewModel viewModel;
    private MusicArchiveAdapter adapter;
    private RecyclerView recyclerView;
    private View progressBar;
    private TextView tvEmptyState;
    private TextView tvTotalCount;

    public static GroupArchiveFragment newInstance(Long groupId) {
        GroupArchiveFragment fragment = new GroupArchiveFragment();
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
            Log.d(TAG, "전달받은 groupId: " + groupId);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_archive, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupToolbar(view);
        setupRecyclerView();
        setupViewModel();
        loadData();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        tvTotalCount = view.findViewById(R.id.tvTotalCount);
    }

    private void setupToolbar(View view) {
        TextView toolbarTitle = view.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("보관함");

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigateUp();
        });
    }

    private void setupRecyclerView() {
        adapter = new MusicArchiveAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

//        // 아이템 클릭 리스너
//        adapter.setOnMusicClickListener(music -> {
//            Log.d(TAG, "음악 클릭: " + music.getTitle());
//            navigateToPostDetail(music.getPostId());
//        });

        // 페이징 처리 (스크롤 끝에 도달하면 추가 로드)
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && dy > 0) {
                    int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                    int totalItemCount = layoutManager.getItemCount();

                    if (lastVisibleItem >= totalItemCount - 3) {
                        viewModel.loadNextPage();
                    }
                }
            }
        });
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(GroupArchiveViewModel.class);

        viewModel.getMusicArchive().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            switch (resource.status) {
                case LOADING:
                    showLoading();
                    break;

                case SUCCESS:
                    hideLoading();
                    if (resource.data != null && !resource.data.isEmpty()) {
                        adapter.submitList(resource.data);
                        updateTotalCount(resource.data.size());
                        showContent();
                    } else {
                        showEmptyState();
                    }
                    break;

                case ERROR:
                    hideLoading();
                    showError(resource.message);
                    break;
            }
        });

        viewModel.getLoadMoreState().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS) {
                if (resource.data != null && !resource.data.isEmpty()) {
                    adapter.addItems(resource.data);
                    // 전체 아이템 수 업데이트
                    updateTotalCount(adapter.getItemCount());
                }
            }
        });
    }

    private void loadData() {
        if (groupId != null) {
            viewModel.loadMusicArchive(groupId);
        } else {
            showError("그룹 정보를 찾을 수 없습니다");
        }
    }

    private void updateTotalCount(int count) {
        if (tvTotalCount != null) {
            tvTotalCount.setText("총 " + count + "곡");
        }
    }

//    private void navigateToPostDetail(Long postId) {
//        if (postId == null) {
//            Toast.makeText(requireContext(), "포스트 정보를 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        try {
//            Bundle args = new Bundle();
//            args.putLong("postId", postId);
//
//            NavController navController = Navigation.findNavController(requireView());
//            // postDetailFragment가 nav_main.xml에 있다고 가정
//            navController.navigate(R.id.postDetailFragment, args);
//        } catch (Exception e) {
//            Log.e(TAG, "포스트 상세 이동 실패", e);
//            Toast.makeText(requireContext(), "화면 전환에 실패했습니다", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    private void showContent() {
        recyclerView.setVisibility(View.VISIBLE);
        tvEmptyState.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        recyclerView.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.VISIBLE);
        tvEmptyState.setText("아직 공유된 음악이 없어요");
        updateTotalCount(0);
    }

    private void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        tvEmptyState.setVisibility(View.VISIBLE);
        tvEmptyState.setText("음악을 불러올 수 없습니다");
    }
}