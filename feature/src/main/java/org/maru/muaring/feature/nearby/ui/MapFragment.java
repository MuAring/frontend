package org.maru.muaring.feature.nearby.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.maru.muaring.feature.R;
import org.maru.muaring.feature.nearby.ui.model.Music;

import java.util.Arrays;
import java.util.List;

public class MapFragment extends Fragment {

    private RecyclerView rvMusic;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_music, container, false);
        rvMusic = view.findViewById(R.id.rvMusic);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Music> musicList = Arrays.asList(
                new Music("Runaway Baby", "Bruno Mars", R.drawable.album_image),
                new Music("Runaway Baby", "Bruno Mars", R.drawable.album_image),
                new Music("Runaway Baby", "Bruno Mars", R.drawable.album_image)
        );

        MusicAdapter adapter = new MusicAdapter(musicList);
        rvMusic.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMusic.setAdapter(adapter);
    }
}