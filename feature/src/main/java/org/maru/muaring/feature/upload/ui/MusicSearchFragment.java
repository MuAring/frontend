package org.maru.muaring.feature.upload.ui;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.maru.muaring.core.common.Callback;
import org.maru.muaring.data.api.dto.SpotifyTrackResponse;
import org.maru.muaring.data.repository.UploadRepository;
import org.maru.muaring.feature.R;
import org.maru.muaring.feature.common.SearchBarFragment;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MusicSearchFragment extends Fragment {

    @Inject
    UploadRepository uploadRepository;

    private MusicSearchAdapter adapter;

    public MusicSearchFragment(){
        super(R.layout.fragment_upload_search);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recycler = view.findViewById(R.id.musicSearchResult);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MusicSearchAdapter();
        recycler.setAdapter(adapter);

        // 검색창 fragment 찾아오기
        SearchBarFragment searchBar =
                (SearchBarFragment) getChildFragmentManager().findFragmentById(R.id.fragmentSearchBar);

        if (searchBar != null) {
            searchBar.setHint("노래를 검색해보세요");

            searchBar.setOnSearchClickListener(query -> {
                if (query.trim().isEmpty()) return;

                uploadRepository.searchMusic(query, new Callback<>() {
                    @Override
                    public void onSuccess(List<SpotifyTrackResponse> result) {
                        adapter.updateList(result);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("MusicSearch", "실패: " + e.getMessage());
                    }
                });
            });
        }
    }
}
