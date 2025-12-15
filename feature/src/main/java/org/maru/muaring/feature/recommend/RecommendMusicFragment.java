package org.maru.muaring.feature.recommend;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.dto.DailyTopMusicResponse;
import org.maru.muaring.feature.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RecommendMusicFragment extends Fragment {

    private View root;
    private RecommendMusicViewModel viewModel;
    private RecommendMusicAdapter adapter;

    private TextView txtTitle;
    private RecyclerView recycler;

    public RecommendMusicFragment() {
        super(R.layout.fragment_recommend_music);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        root = view;
        txtTitle = view.findViewById(R.id.txt_title);
        recycler = view.findViewById(R.id.recycler_recommend_music);

        // 처음엔 숨김 (로딩 성공하면 보여줌)
        root.setVisibility(View.GONE);

        adapter = new RecommendMusicAdapter();
        recycler.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recycler.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(RecommendMusicViewModel.class);

        viewModel.top3Last7Days.observe(getViewLifecycleOwner(), res -> {
            if (res == null) return;

            switch (res.status) {
                case SUCCESS:
                    if (res.data != null && res.data.getMusics() != null && !res.data.getMusics().isEmpty()) {
                        root.setVisibility(View.VISIBLE);
                        adapter.submitList(res.data.getMusics());
                    } else {
                        // 데이터 비었으면 숨김
                        root.setVisibility(View.GONE);
                        adapter.submitList(null);
                    }
                    break;

                case ERROR:
                    // API 터지면 아예 안 보이게
                    root.setVisibility(View.GONE);
                    adapter.submitList(null);
                    break;

                case LOADING:
                default:
                    break;
            }
        });

        // 지난 7일 TOP3만
        viewModel.loadTop3Last7Days();
    }
}
