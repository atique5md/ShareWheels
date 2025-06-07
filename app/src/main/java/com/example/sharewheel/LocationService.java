package com.example.sharewheel;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class LocationService extends Service {

    private static final String CHANNEL_ID = "foreground_service_channel";
    private static final float MIN_DISTANCE_THRESHOLD = 10.0f;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private double lastLatitude = 0.0;
    private double lastLongitude = 0.0;

    private String userId;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        initLocationRequest();

        // ✅ Load userId from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        userId = sharedPreferences.getString("user_id", null);

        if (userId == null || userId.isEmpty()) {
            Log.e("LocationService", "❌ No user ID found. Stopping service.");
            stopSelf();
        } else {
            Log.d("LocationService", "✅ User ID loaded: " + userId);
        }
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e("LocationService", "❌ Location permission not granted.");
            stopSelf();
            return START_NOT_STICKY;
        }

        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
        );

        // Foreground notification
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("🔍 Location Tracking")
                .setContentText("Tracking your location...")
                .setSmallIcon(R.drawable.ic_home)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setOngoing(true)
                .build();

        startForeground(1, notification);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
            Log.d("LocationService", "✅ Location updates stopped");
        }
    }

    private void initLocationRequest() {
        locationRequest = new LocationRequest.Builder(
                LocationRequest.PRIORITY_HIGH_ACCURACY,
                10_000L
        )
                .setMinUpdateDistanceMeters(MIN_DISTANCE_THRESHOLD)
                .setMinUpdateIntervalMillis(5_000L)
                .build();
    }

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) return;

            for (Location location : locationResult.getLocations()) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();

                float[] results = new float[1];
                Location.distanceBetween(lastLatitude, lastLongitude, lat, lng, results);
                float distanceInMeters = results[0];

                if (distanceInMeters >= MIN_DISTANCE_THRESHOLD) {
                    lastLatitude = lat;
                    lastLongitude = lng;

                    Log.d("LocationService", "✅ Location Changed - Lat: " + lat + ", Lng: " + lng);

                    // 📝 Optionally store this in SharedPreferences or Room DB
                    saveLastLocationToPrefs(lat, lng);
                } else {
                    Log.d("LocationService", "⏩ Skipped update. Distance moved: " + distanceInMeters + "m");
                }
            }
        }
    };

    private void saveLastLocationToPrefs(double lat, double lng) {
        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("last_latitude", String.valueOf(lat));
        editor.putString("last_longitude", String.valueOf(lng));
        editor.putLong("last_updated", System.currentTimeMillis());
        editor.apply();

        Log.d("LocationService", "📍 Location saved to SharedPreferences");
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Location Tracking Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }
}
