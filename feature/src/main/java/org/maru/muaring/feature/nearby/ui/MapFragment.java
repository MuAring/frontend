package org.maru.muaring.feature.nearby.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.camera.CameraUpdate;
import com.kakao.vectormap.camera.CameraUpdateFactory;

import org.maru.muaring.core.common.Callback;
import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.dto.LocationRequestDTO;
import org.maru.muaring.data.api.dto.TodayNearbyMusicDTO;
import org.maru.muaring.data.repository.LocationRepository;
import org.maru.muaring.data.repository.NearbyRepository;
import org.maru.muaring.feature.R;
import org.maru.muaring.feature.nearby.ui.model.Music;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MapFragment extends Fragment {

    private MusicAdapter adapter;
    private final List<Music> musicList = new java.util.ArrayList<>();

    private MapView mapView;
    private KakaoMap kakaoMap;
    private RecyclerView rvMusic;
    private ImageButton btnShowMusic;
    private CardView musicCard;
    private TextView tvCount;

    private static final int LOCATION_PERMISSION_REQUEST = 1001;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Location lastSentLocation = null;
    private static final float MIN_DISTANCE = 20f;
    @Inject
    LocationRepository locationRepository;

    @Inject
    LocationManager locationManager;


    @Inject
    NearbyRepository nearbyRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        rvMusic = view.findViewById(R.id.rvMusic);
        btnShowMusic = view.findViewById(R.id.btnShowMusic);
        musicCard = view.findViewById(R.id.musicCard);
        tvCount = view.findViewById(R.id.tvCount);

        mapView = view.findViewById(R.id.map_view);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        btnShowMusic.setOnClickListener(v -> {
            if (musicCard.getVisibility() == View.INVISIBLE) {
                musicCard.setVisibility(View.VISIBLE);
            } else {
                musicCard.setVisibility(View.INVISIBLE);
            }
        });

        mapView.start(new MapLifeCycleCallback() {
            @Override public void onMapDestroy() {}

            @Override
            public void onMapError(@Nullable Exception e) {
                if (e != null) e.printStackTrace();
            }
        }, new KakaoMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull KakaoMap map) {
                kakaoMap = map;
                initMap();

                Location location = locationManager.getLastKnownLocation();
                if (location != null) {
                    moveCameraToLocation(location);
                }
            }
        });

        return view;
    }

    private void initMap() {
        if (kakaoMap == null) return;

        LatLng seoul = LatLng.from(37.5665, 126.9780);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCenterPosition(seoul, 3);
        kakaoMap.moveCamera(cameraUpdate);
    }

    private void moveCameraToLocation(Location location) {
        if (kakaoMap == null) return;

        LatLng current = LatLng.from(
                location.getLatitude(),
                location.getLongitude()
        );

        CameraUpdate cameraUpdate =
                CameraUpdateFactory.newCenterPosition(current, 16);

        kakaoMap.moveCamera(cameraUpdate);
    }

    @Override
    public void onResume() {
        super.onResume();

        Location location = locationManager.getLastKnownLocation();
        if (location == null) return;

        fetchTodayNearbyMusic(
                location.getLatitude(),
                location.getLongitude(),
                0.3
        );
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new MusicAdapter(requireContext(), musicList, memberId -> {
            Bundle bundle = new Bundle();
            bundle.putLong("memberId", memberId);

            NavHostFragment
                    .findNavController(this)
                    .navigate(R.id.memberProfileFragment, bundle);
        });

        rvMusic.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMusic.setAdapter(adapter);
    }
    private void fetchTodayNearbyMusic(double lat, double lng, double radiusKm) {

        nearbyRepository
                .getTodayNearbyMusic(lat, lng, radiusKm)
                .observe(getViewLifecycleOwner(), resource -> {

                    if (resource.status == Resource.Status.SUCCESS
                            && resource.data != null) {

                        musicList.clear();

                        for (TodayNearbyMusicDTO dto : resource.data) {
                            musicList.add(new Music(
                                    dto.getMemberId(),
                                    dto.getProfileImageUrl(),
                                    dto.getMusicName(),
                                    dto.getArtistName(),
                                    dto.getAlbumImageUrl()
                            ));
                        }

                        adapter.notifyDataSetChanged();

                        int count = musicList.size();
                        tvCount.setText(String.valueOf(count));
                        tvCount.setText(count == 0 ? "0" : String.valueOf(count));

                        return;
                    }

                    if (resource.status == Resource.Status.ERROR) {
                        Log.e("Nearby", "인근 음악 조회 실패: " + resource.message);
                    }
                });
    }


}