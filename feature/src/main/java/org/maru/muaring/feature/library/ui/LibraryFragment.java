package org.maru.muaring.feature.library.ui;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private TextView tvTotalCount, btnSelectAll, btnClearSelect;
    private LibraryAdapter adapter;
    private LinearLayout bottomActionBar;
    private ImageView btnDelete;

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
        bottomActionBar = view.findViewById(R.id.bottomActionBar);
        btnDelete = view.findViewById(R.id.btnDelete);
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

            if (selectedCount > 0) {
                bottomActionBar.setVisibility(View.VISIBLE);
            } else {
                bottomActionBar.setVisibility(View.GONE);
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

        btnDelete.setOnClickListener(v -> showDeleteDialog());
    }

    private void showDeleteDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_confirm);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // 화면 어둡게
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.2f;
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        dialog.setCancelable(false);

        Button btnYes = dialog.findViewById(R.id.btnYes);
        Button btnNo = dialog.findViewById(R.id.btnNo);

        btnNo.setOnClickListener(v -> dialog.dismiss());

        btnYes.setOnClickListener(v -> {
            // 선택 음악 삭제 //
            dialog.dismiss();
        });

        dialog.show();
    }

}
