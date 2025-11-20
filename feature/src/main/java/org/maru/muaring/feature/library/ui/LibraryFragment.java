package org.maru.muaring.feature.library.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.maru.muaring.feature.R;
import org.maru.muaring.feature.nearby.ui.model.Music;

import java.util.Arrays;
import java.util.List;

public class LibraryFragment extends Fragment {

    private RecyclerView rvLibrary;
    private TextView tvTotalCount;
    private TextView btnSelectAll;
    private TextView btnClearSelect;

    private LibraryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_library, container, false);

        rvLibrary = view.findViewById(R.id.rvArchiveMusic);
        tvTotalCount = view.findViewById(R.id.tvTotalCount);
        btnSelectAll = view.findViewById(R.id.btnSelectAll);
        btnClearSelect = view.findViewById(R.id.btnClearSelect);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Music> musicList = Arrays.asList(
                new Music("Runaway Baby", "Bruno Mars", R.drawable.album_image),
                new Music("Attention", "Charlie Puth", R.drawable.album_image),
                new Music("Perfect", "Ed Sheeran", R.drawable.album_image)
        );

        int totalCount = musicList.size();
        tvTotalCount.setText("총 " + totalCount + "곡");

        adapter = new LibraryAdapter(musicList, selectedCount -> {
            if (selectedCount == 0) {
                tvTotalCount.setText("총 " + totalCount + "곡");
            } else {
                tvTotalCount.setText("총 " + selectedCount + "곡");
            }
        });

        rvLibrary.setLayoutManager(new LinearLayoutManager(getContext()));
        rvLibrary.setAdapter(adapter);

        btnSelectAll.setOnClickListener(v -> {
            adapter.selectAll();
        });

        btnClearSelect.setOnClickListener(v -> {
            adapter.clearSelection();
        });
    }
}
