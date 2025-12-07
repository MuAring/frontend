package org.maru.muaring.feature.upload.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import org.maru.muaring.core.common.Callback;
import org.maru.muaring.data.api.dto.MusicPostRequest;
import org.maru.muaring.data.api.dto.MyGroupListResponse;
import org.maru.muaring.data.api.dto.MyGroupSummary;
import org.maru.muaring.data.api.dto.SpotifyTrackResponse;
import org.maru.muaring.data.repository.UploadRepository;
import org.maru.muaring.feature.R;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UploadFragment extends Fragment {

    private SpotifyTrackResponse selectedMusic;
    @Inject
    UploadRepository uploadRepository;
    List<MyGroupSummary> groupList = new ArrayList<>();
    Long selectedGroupId = null;

    public UploadFragment() {
        super(R.layout.fragment_upload);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Spinner spinnerGroup = view.findViewById(R.id.spinnerGroup);
        Button btnPost = view.findViewById(R.id.btnPost);
        EditText edtText = view.findViewById(R.id.edtText);

        View toolbar = view.findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setText("오늘의 음악");

        ImageButton btnBack = toolbar.findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        if (getArguments() != null) {
            selectedMusic = getArguments().getParcelable("selectedMusic");
        }
        if (selectedMusic != null) {
            bindSelectedMusic(view);
        }

        btnPost.setOnClickListener(v -> {

            String content = edtText.getText().toString().trim();

            if (content.isEmpty()) {
                Toast.makeText(requireContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            Long groupIdToSend = selectedGroupId;

            String spotifyId = selectedMusic.getSpotifyId();

            MusicPostRequest request = new MusicPostRequest(
                    groupIdToSend,
                    spotifyId,
                    content
            );

            uploadRepository.createMusicPost(request, new Callback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Toast.makeText(requireContext(), "게시글 등록 완료!", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed(); // 화면 뒤로가기
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(requireContext(), "등록 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("UploadFragment", "등록 실패: " + e.getMessage());
                }
            });
        });

        uploadRepository.getMyGroups(new Callback<MyGroupListResponse>() {
            @Override
            public void onSuccess(MyGroupListResponse result) {

                groupList = result.getGroups();

                List<String> spinnerItems = new ArrayList<>();
                spinnerItems.add("선택 안 함"); // 첫 번째 옵션 고정

                for (MyGroupSummary g : groupList) {
                    spinnerItems.add(g.getName());
                }

                ArrayAdapter<String> spinnerAdapter =
                        new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, spinnerItems);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinnerGroup.setAdapter(spinnerAdapter);

                spinnerGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
                        if (pos == 0) {
                            selectedGroupId = null;
                        } else {
                            selectedGroupId = groupList.get(pos - 1).getGroupId();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        selectedGroupId = null;
                    }
                });

                Log.d("UploadFragment", "그룹 수 = " + result.getTotalCount());
            }

            @Override
            public void onError(Exception e) {
                Log.e("UploadFragment", "그룹 불러오기 실패: " + e.getMessage());
            }
        });
    }

    private void bindSelectedMusic(View root) {

        View musicView = root.findViewById(R.id.selectedMusic);

        ImageView albumImage = musicView.findViewById(R.id.ivAlbum);
        TextView tvTitle = musicView.findViewById(R.id.tvTitle);
        TextView tvArtist = musicView.findViewById(R.id.tvArtist);

        tvTitle.setText(selectedMusic.getName());
        tvArtist.setText(selectedMusic.getArtistName());

        Glide.with(this)
                .load(selectedMusic.getAlbumImgUrl())
                .placeholder(R.drawable.album_image)
                .into(albumImage);
    }
}
