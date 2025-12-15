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
            @Override
            public void onMapDestroy() {}

            @Override
            public void onMapError(@Nullable Exception e) {
                if (e != null) e.printStackTrace();
            }
        }, new KakaoMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull KakaoMap map) {
                kakaoMap = map;
                initMap();
                checkPermission();
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

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST
            );
            return;
        }
        startLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            }
        }
    }

    private void startLocationUpdates() {

        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(2000)
                .setFastestInterval(1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {
                Location location = result.getLastLocation();
                if (location == null || kakaoMap == null) return;

                double lat = location.getLatitude();
                double lng = location.getLongitude();

                LatLng current = LatLng.from(lat, lng);
                kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(current, 16));

                if (lat == 0.0 && lng == 0.0) {
                    Log.w("Location", "아직 유효한 위치 아님");
                    return;
                }

                if (lastSentLocation == null) {
                    lastSentLocation = new Location(location);
                    sendLocation(lat, lng);
                    return;
                }

                float distance = location.distanceTo(lastSentLocation);

                if (distance >= MIN_DISTANCE) {
                    lastSentLocation = location;
                    sendLocation(lat, lng);
                    Log.d("LOCATION", "20m 이상 이동: 서버 전송됨 (" + distance + "m)");
                } else {
                    Log.d("LOCATION", "20m 미만 이동: 전송 안함 (" + distance + "m)");
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
            );
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    private void sendLocation(double lat, double lng) {
        LocationRequestDTO request = new LocationRequestDTO(lat, lng);

        locationRepository.updateLocation(request, new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.d("Location", "위치 전송 성공");
                fetchTodayNearbyMusic(lat, lng, 0.3);
            }

            @Override
            public void onError(Exception e) {
                Log.e("Location", "위치 전송 실패: " + e.getMessage());
            }
        });
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