package org.maru.muaring.feature.library.ui;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.maru.muaring.data.api.LibraryApi;
import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.ExportRequest;
import org.maru.muaring.data.api.dto.LibraryDeleteRequest;
import org.maru.muaring.data.api.dto.LibraryMusicListRequestDto;
import org.maru.muaring.data.api.dto.LibraryMusicListResponseDto;
import org.maru.muaring.feature.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class LibraryFragment extends Fragment {

    private RecyclerView rvLibrary;
    private TextView tvTotalCount, btnSelectAll, btnClearSelect;
    private LibraryAdapter adapter;
    private LinearLayout bottomActionBar;
    private ImageView btnDelete, btnSpotify;
    @Inject
    LibraryApi libraryApi;

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
        btnSpotify = view.findViewById(R.id.btnSpotify);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadLibrary();

        btnSelectAll.setOnClickListener(v -> {
            adapter.selectAll();
        });

        btnClearSelect.setOnClickListener(v -> {
            adapter.clearSelection();
        });

        btnSpotify.setOnClickListener(v -> {
            List<Long> selectedIds = adapter.getSelectedLibraryIds();

            ExportRequest request = new ExportRequest(selectedIds);

            libraryApi.exportToSpotify(request).enqueue(new Callback<Void>() {

                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(), "스포티파이에 추가 완료", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "응답 오류: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(requireContext(), "서버 오류 발생: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
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

            List<Long> selectedIds = adapter.getSelectedLibraryIds();

            if (selectedIds.isEmpty()) {
                Log.d("LibraryDelete", "선택된 음악이 없습니다.");
                dialog.dismiss();
                return;
            }

            final Dialog deleteDialog = dialog;

            libraryApi.deleteMusic(selectedIds).enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                    Log.d("LibraryDelete", "삭제 요청 ID들: " + selectedIds);

                    if (response.isSuccessful()) {
                        Log.d("LibraryDelete", "서버에서 삭제 성공: " + selectedIds);
                        loadLibrary();
                        deleteDialog.dismiss();

                    } else {
                        Log.e("LibraryDelete", "삭제 실패: HTTP " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                    Log.e("LibraryDelete", "서버 오류: " + t.getMessage());
                }
            });
        });
        dialog.show();
    }

    private void loadLibrary() {
        libraryApi.getLibrary().enqueue(new Callback<ApiResponse<LibraryMusicListResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<LibraryMusicListResponseDto>> call,
                                   Response<ApiResponse<LibraryMusicListResponseDto>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Log.e("Library", "보관함 불러오기 실패");
                    return;
                }

                List<LibraryMusicListRequestDto> dtoList =
                        response.body().getData().musicList;

                List<Music> musicList = new ArrayList<>();
                for (LibraryMusicListRequestDto dto : dtoList) {
                    musicList.add(new Music(
                            dto.libraryId,
                            dto.musicId,
                            dto.title,
                            dto.artist,
                            dto.albumImage
                    ));
                }

                tvTotalCount.setText("총 " + musicList.size() + "곡");

                adapter = new LibraryAdapter(musicList, selectedCount -> {
                    if (selectedCount == 0) {
                        tvTotalCount.setText("총 " + musicList.size() + "곡");
                    } else {
                        tvTotalCount.setText("총 " + selectedCount + "곡");
                    }

                    bottomActionBar.setVisibility(selectedCount > 0 ? View.VISIBLE : View.GONE);
                });

                rvLibrary.setLayoutManager(new LinearLayoutManager(getContext()));
                rvLibrary.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<ApiResponse<LibraryMusicListResponseDto>> call, Throwable t) {
                Log.e("Library", "서버 오류: " + t.getMessage());
            }
        });
    }


}
