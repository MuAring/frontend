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

import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.camera.CameraUpdate;
import com.kakao.vectormap.camera.CameraUpdateFactory;

import org.maru.muaring.feature.R;
import org.maru.muaring.feature.nearby.ui.model.Music;

import java.util.Arrays;
import java.util.List;

public class MapFragment extends Fragment {

    private MapView mapView;
    private KakaoMap kakaoMap;
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

        mapView = view.findViewById(R.id.map_view);

        btnShowMusic.setOnClickListener(v -> {
            if (musicCard.getVisibility() == View.INVISIBLE) {
                musicCard.setVisibility(View.VISIBLE);
            } else {
                musicCard.setVisibility(View.INVISIBLE);
            }
        });

        mapView.start(
                new MapLifeCycleCallback() {
                    @Override
                    public void onMapDestroy() {
                        // 지도 종료 시
                    }

                    @Override
                    public void onMapError(@Nullable Exception e) {
                        // 인증 실패 등 에러 시
                        if (e != null) {
                            e.printStackTrace();
                        }
                    }
                },
                new KakaoMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull KakaoMap map) {
                        kakaoMap = map;

                        // 서울(37.5665, 126.9780)로 중심, 줌레벨 3 설정
                        LatLng seoul = LatLng.from(37.5665, 126.9780);
                        CameraUpdate cameraUpdate =
                                CameraUpdateFactory.newCenterPosition(seoul, 3);
                        kakaoMap.moveCamera(cameraUpdate);
                    }
                }
        );

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