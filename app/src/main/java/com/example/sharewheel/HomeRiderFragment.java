package com.example.sharewheel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import androidx.appcompat.widget.SearchView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HomeRiderFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private SearchView searchView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Button btnGetLocation;

    private LatLng sourceLatLng = null;
    private LatLng destinationLatLng = null;
    private String sourceAddress = "";
    private String destinationAddress = "";

    public HomeRiderFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_rider, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchView = view.findViewById(R.id.searchView);
        CardView cardView = view.findViewById(R.id.cardViewSearch);
        btnGetLocation = view.findViewById(R.id.btn_get_location);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_container);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction().replace(R.id.map_container, mapFragment).commit();
        }

        Button btnStartRide = view.findViewById(R.id.btnStartRide);

        btnStartRide.setOnClickListener(v -> {
            if (sourceLatLng == null || destinationLatLng == null) {
                Toast.makeText(getContext(), "Get your location and search destination first", Toast.LENGTH_SHORT).show();
                return;
            }

            float[] results = new float[1];
            Location.distanceBetween(
                    sourceLatLng.latitude, sourceLatLng.longitude,
                    destinationLatLng.latitude, destinationLatLng.longitude,
                    results
            );
            float distanceKm = results[0] / 1000f;
            int price = (int) (distanceKm * 10); // ₹10/km

            // Save ride data to SharedPreferences
            SharedPreferences prefs = requireContext().getSharedPreferences("ride_data", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("source_address", sourceAddress);
            editor.putString("destination_address", destinationAddress);
            editor.putFloat("distance", distanceKm);
            editor.putInt("price", price);
            editor.apply();

            // AlertDialog to show details
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dalog_rider, null);
            builder.setView(dialogView);

            TextView tvSource = dialogView.findViewById(R.id.tvSource);
            TextView tvDestination = dialogView.findViewById(R.id.tvDestination);
            TextView tvDistance = dialogView.findViewById(R.id.tvDistance);
            TextView tvPrice = dialogView.findViewById(R.id.tvPrice);
            Button okButton = dialogView.findViewById(R.id.dialogOkBtn);

            tvSource.setText("Source: " + sourceAddress);
            tvDestination.setText("Destination: " + destinationAddress);
            tvDistance.setText(String.format(Locale.getDefault(), "Distance: %.2f km", distanceKm));
            tvPrice.setText("Price: ₹" + price);

            AlertDialog dialog = builder.create();
            okButton.setOnClickListener(dialogView1 -> {
                dialog.dismiss();
                Toast.makeText(getContext(), "Ride request sent to driver", Toast.LENGTH_SHORT).show();
                // Optional: Switch to driver side
                // startActivity(new Intent(getContext(), DriverActivity.class));
            });

            dialog.show();
        });

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap gMap) {
                googleMap = gMap;
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                }
                LatLng india = new LatLng(20.5937, 78.9629);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(india, 5));
            }
        });

        btnGetLocation.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                fetchAndShowCurrentLocation();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchAndMarkLocation(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void fetchAndShowCurrentLocation() {
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {
            Toast.makeText(getContext(), "Please enable location services", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        } else {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null && googleMap != null) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.clear();
                    googleMap.addMarker(new MarkerOptions().position(currentLatLng).title("Your Location"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16));

                    sourceLatLng = currentLatLng;

                    Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        if (addresses != null && !addresses.isEmpty()) {
                            sourceAddress = addresses.get(0).getAddressLine(0);
                            Toast.makeText(getContext(), "Source: " + sourceAddress, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "Location found but address unavailable", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Geocoder failed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Unable to fetch location", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void searchAndMarkLocation(String locationName) {
        if (locationName == null || locationName.isEmpty()) return;

        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(latLng).title(locationName));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));

                destinationLatLng = latLng;
                destinationAddress = address.getAddressLine(0);

                Toast.makeText(getContext(), "Destination: " + destinationAddress, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "No results found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to fetch location", Toast.LENGTH_SHORT).show();
        }
    }
}
