package com.example.sharewheel;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class HomeDriverFragment extends Fragment {

    private SearchView searchView;
    private GoogleMap googleMap;

    public HomeDriverFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_driver, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CardView cardView = view.findViewById(R.id.cardViewSearch);
        searchView = view.findViewById(R.id.searchView);

        // Optional: CardView click to expand search view
        cardView.setOnClickListener(v -> searchView.setIconified(false));

        // Initialize map fragment inside map_container
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.map_container, mapFragment)
                .commit();

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap gMap) {
                googleMap = gMap;
                googleMap.getUiSettings().setZoomControlsEnabled(true);

                // Move to India's center initially
                LatLng india = new LatLng(30.2689, 77.9931);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(india, 4));
            }
        });

        searchView.setQueryHint("Search for a location");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query == null || query.trim().isEmpty()) {
                    Toast.makeText(getContext(), "Please enter a location", Toast.LENGTH_SHORT).show();
                    return false;
                }

                Geocoder geocoder = new Geocoder(getContext());
                try {
                    List<Address> addresses = geocoder.getFromLocationName(query, 1);
                    if (addresses == null || addresses.size() == 0) {
                        Toast.makeText(getContext(), "Location not found", Toast.LENGTH_SHORT).show();
                    } else {
                        Address address = addresses.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                        if (googleMap != null) {
                            googleMap.clear();
                            googleMap.addMarker(new MarkerOptions().position(latLng).title(query));
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f));
                        }

                        // Toast the searched location
                        Toast.makeText(getContext(), "Searched Location: " + query, Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Geocoder service not available", Toast.LENGTH_SHORT).show();
                }

                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Optional: you can handle live search here if needed
                return false;
            }
        });
    }
}
