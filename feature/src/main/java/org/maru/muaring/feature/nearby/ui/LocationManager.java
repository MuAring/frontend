package org.maru.muaring.feature.nearby.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.maru.muaring.core.common.Callback;
import org.maru.muaring.data.api.dto.LocationRequestDTO;
import org.maru.muaring.data.repository.LocationRepository;

import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import jakarta.inject.Inject;

@Singleton
public class LocationManager {
    private static final String TAG = "LocationManager";

    private static final float MIN_DISTANCE = 20f; // 20m
    private static final long HEARTBEAT_INTERVAL = 2 * 60 * 1000; // 2분

    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;
    private final LocationRepository locationRepository;

    private Location lastSentLocation = null;
    private long lastHeartbeatTime = 0;

    private LocationCallback locationCallback;
    private Location lastKnownLocation;

    @Inject
    public LocationManager(
            @ApplicationContext Context context,
            LocationRepository locationRepository
    ) {
        this.context = context.getApplicationContext();
        this.locationRepository = locationRepository;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        initLocationCallback();
    }

    public void start() {
        if (!hasPermission()) {
            Log.w(TAG, "위치 권한 없음 → start 중단");
            return;
        }

        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(2000)
                .setFastestInterval(1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        try {
            fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
            );
            Log.d(TAG, "위치 업데이트 시작");
        } catch (SecurityException e) {
            Log.e(TAG, "위치 권한 없음 (SecurityException)", e);
        }
    }


    public void stop() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
        Log.d(TAG, "위치 업데이트 중단");
    }

    private void initLocationCallback() {

        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(@NonNull LocationResult result) {

                Location location = result.getLastLocation();
                if (location == null) return;

                lastKnownLocation = location;

                double lat = location.getLatitude();
                double lng = location.getLongitude();

                if (lat == 0.0 && lng == 0.0) {
                    Log.w(TAG, "유효하지 않은 위치");
                    return;
                }

                long now = System.currentTimeMillis();

                if (now - lastHeartbeatTime >= HEARTBEAT_INTERVAL) {
                    sendLocation(lat, lng);
                    lastHeartbeatTime = now;
                    Log.d(TAG, "Heartbeat 전송");
                    return;
                }

                if (lastSentLocation == null) {
                    lastSentLocation = new Location(location);
                    sendLocation(lat, lng);
                    Log.d(TAG, "최초 위치 전송");
                    return;
                }

                float distance = location.distanceTo(lastSentLocation);
                if (distance >= MIN_DISTANCE) {
                    lastSentLocation = new Location(location);
                    sendLocation(lat, lng);
                    Log.d(TAG, "20m 이동 → 위치 전송 (" + distance + "m)");
                }
            }
        };
    }

    private void sendLocation(double lat, double lng) {

        LocationRequestDTO request = new LocationRequestDTO(lat, lng);

        locationRepository.updateLocation(request, new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.d(TAG, "위치 전송 성공");
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "위치 전송 실패", e);
            }
        });
    }

    private boolean hasPermission() {
        return ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
    }

    public @Nullable Location getLastKnownLocation() {
        return lastSentLocation;
    }

}

