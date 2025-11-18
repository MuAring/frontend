package org.maru.muaring.feature.nearby.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.maru.muaring.feature.R;
import org.maru.muaring.feature.nearby.ui.model.Music;

import java.util.Arrays;
import java.util.List;

public class MapFragment extends Fragment {

    private RecyclerView rvMusic;
    private ImageButton btnShowMusic;
    private CardView musicCard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        rvMusic = view.findViewById(R.id.rvMusic);
        btnShowMusic = view.findViewById(R.id.btnShowMusic);
        musicCard = view.findViewById(R.id.musicCard);

        btnShowMusic.setOnClickListener(v -> {
            if (musicCard.getVisibility() == View.INVISIBLE) {
                musicCard.setVisibility(View.VISIBLE);
            } else {
                musicCard.setVisibility(View.INVISIBLE);
            }
        });

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