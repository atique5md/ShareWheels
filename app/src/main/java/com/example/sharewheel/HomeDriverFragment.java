package com.example.sharewheel;

import static android.content.ContentValues.TAG;

import static com.example.sharewheel.PolylineDecoder.decodePolyline;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import com.google.gson.Gson;
import android.graphics.Color;


public class HomeDriverFragment extends Fragment {

    private SearchView searchView;
    private GoogleMap googleMap;
    private final Gson gson = new Gson();
    private Polyline currentRoute;


    public HomeDriverFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_driver, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CardView cardView = view.findViewById(R.id.cardViewSearch);
        searchView = view.findViewById(R.id.searchView);

        // Expand SearchView on CardView click
        cardView.setOnClickListener(v -> searchView.setIconified(false));

        // Setup Map Fragment
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getChildFragmentManager().beginTransaction().replace(R.id.map_container, mapFragment).commit();

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap gMap) {
                googleMap = gMap;
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                LatLng india = new LatLng(30.2689, 77.9931);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(india, 5));

                // Load and display markers if ride data exists
                showRideIfExists();
            }
        });

        // Handle location search
        searchView.setQueryHint("Search for a location");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query == null || query.trim().isEmpty()) {
                    Toast.makeText(getContext(), "Please enter a location", Toast.LENGTH_SHORT).show();
                    return false;
                }

                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocationName(query, 1);
                    if (addresses == null || addresses.isEmpty()) {
                        Toast.makeText(getContext(), "Location not found", Toast.LENGTH_SHORT).show();
                    } else {
                        Address address = addresses.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                        if (googleMap != null) {
                            googleMap.clear();
                            googleMap.addMarker(new MarkerOptions().position(latLng).title(query));
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f));
                        }

                        Toast.makeText(getContext(), "Searched Location: " + query, Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Geocoder error", Toast.LENGTH_SHORT).show();
                }

                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }


    private void showRideIfExists() {
        if (getView() == null) return;

        LinearLayout rideRequestContainer = getView().findViewById(R.id.rideRequestContainer);
        TextView tvSource = getView().findViewById(R.id.tvSource);
        TextView tvDestination = getView().findViewById(R.id.tvDestination);
        TextView tvDistance = getView().findViewById(R.id.tvDistance);
        TextView tvPrice = getView().findViewById(R.id.tvPrice);
        Button btnAccept = getView().findViewById(R.id.btnAccept);
        Button btnReject = getView().findViewById(R.id.btnReject);

        SharedPreferences prefs = requireContext().getSharedPreferences("ride_data", Context.MODE_PRIVATE);
        String source = prefs.getString("source_address", null);
        String destination = prefs.getString("destination_address", null);
        float distance = prefs.getFloat("distance", -1f);
        int price = prefs.getInt("price", -1);

        if (source != null && destination != null && distance > 0 && price > 0) {
            rideRequestContainer.setVisibility(View.VISIBLE);
            tvSource.setText("Source: " + source);
            tvDestination.setText("Destination: " + destination);
            tvDistance.setText(String.format(Locale.getDefault(), "Distance: %.2f km", distance));
            tvPrice.setText("Price: ₹" + price);

            // Use geocoder to fetch LatLngs
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            try {
                List<Address> sourceList = geocoder.getFromLocationName(source, 1);
                List<Address> destinationList = geocoder.getFromLocationName(destination, 1);

                if (!sourceList.isEmpty() && !destinationList.isEmpty()) {
                    Address sourceAddress = sourceList.get(0);
                    Address destinationAddress = destinationList.get(0);

                    LatLng sourceLatLng = new LatLng(sourceAddress.getLatitude(), sourceAddress.getLongitude());
                    LatLng destinationLatLng = new LatLng(destinationAddress.getLatitude(), destinationAddress.getLongitude());

                    googleMap.clear();

                    googleMap.addMarker(new MarkerOptions().position(sourceLatLng).title("Source").snippet(source));
                    googleMap.addMarker(new MarkerOptions().position(destinationLatLng).title("Destination").snippet(destination));

                    // Adjust camera
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(sourceLatLng);
                    builder.include(destinationLatLng);
                    LatLngBounds bounds = builder.build();
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 150);
                    googleMap.animateCamera(cameraUpdate);

                    // 🔹 Call drawRoute with fetched LatLngs
                    drawRoute(sourceLatLng, destinationLatLng);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error loading route", Toast.LENGTH_SHORT).show();
            }

        } else {
            rideRequestContainer.setVisibility(View.GONE);
        }

        btnAccept.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Ride Accepted!", Toast.LENGTH_SHORT).show();
            rideRequestContainer.setVisibility(View.GONE);
            prefs.edit().clear().apply();
        });

        btnReject.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Ride Rejected!", Toast.LENGTH_SHORT).show();
            rideRequestContainer.setVisibility(View.GONE);
            prefs.edit().clear().apply();
        });
    }

    //    private void showRideIfExists() {
//        if (getView() == null) return;
//
//        LinearLayout rideRequestContainer = getView().findViewById(R.id.rideRequestContainer);
//        TextView tvSource = getView().findViewById(R.id.tvSource);
//        TextView tvDestination = getView().findViewById(R.id.tvDestination);
//        TextView tvDistance = getView().findViewById(R.id.tvDistance);
//        TextView tvPrice = getView().findViewById(R.id.tvPrice);
//        Button btnAccept = getView().findViewById(R.id.btnAccept);
//        Button btnReject = getView().findViewById(R.id.btnReject);
//
//        SharedPreferences prefs = requireContext().getSharedPreferences("ride_data", Context.MODE_PRIVATE);
//        String source = prefs.getString("source_address", null);
//        String destination = prefs.getString("destination_address", null);
//        float distance = prefs.getFloat("distance", -1f);
//        int price = prefs.getInt("price", -1);
//
//        if (source != null && destination != null && distance > 0 && price > 0) {
//            rideRequestContainer.setVisibility(View.VISIBLE);
//            tvSource.setText("Source: " + source);
//            tvDestination.setText("Destination: " + destination);
//            tvDistance.setText(String.format(Locale.getDefault(), "Distance: %.2f km", distance));
//            tvPrice.setText("Price: ₹" + price);
//
//            showSourceAndDestinationOnMap(source, destination);
//        } else {
//            rideRequestContainer.setVisibility(View.GONE);
//        }
//
//        btnAccept.setOnClickListener(v -> {
//            Toast.makeText(getContext(), "Ride Accepted!", Toast.LENGTH_SHORT).show();
//            rideRequestContainer.setVisibility(View.GONE);
//            prefs.edit().clear().apply();
//        });
//
//        btnReject.setOnClickListener(v -> {
//            Toast.makeText(getContext(), "Ride Rejected!", Toast.LENGTH_SHORT).show();
//            rideRequestContainer.setVisibility(View.GONE);
//            prefs.edit().clear().apply();
//        });
//    }
    private void drawRoute(LatLng origin, LatLng destination) {
        if (origin == null || destination == null) {
            Log.e(TAG, "drawRoute skipped - null origin or destination");
            return;
        }

        Log.d(TAG, "Drawing route from " + origin + " to " + destination);

        String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + origin.latitude + "," + origin.longitude +
                "&destination=" + destination.latitude + "," + destination.longitude +
                "&mode=driving" +
                "&key=" + "Direction API Key Paste";        // important

        VolleyHelper.sendGetRequest(requireContext(), url, new VolleyHelper.VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d(TAG, "Directions API success");



                JsonObject jsonObject = gson.fromJson(response, JsonObject.class);

                JsonArray routes = jsonObject.getAsJsonArray("routes");

                if (routes == null || routes.size() == 0) {
                    Log.e(TAG, "No routes found");
                    return;
                }

                JsonObject route = routes.get(0).getAsJsonObject();
                JsonObject overviewPolyline = route.getAsJsonObject("overview_polyline");
                String encodedPoints = overviewPolyline.get("points").getAsString();

                List<LatLng> routePoints = decodePolyline(encodedPoints);
                Log.d(TAG, "Route decoded with " + routePoints.size() + " points");

                requireActivity().runOnUiThread(() -> {
                    if (currentRoute != null) currentRoute.remove();

                    currentRoute = googleMap.addPolyline(new PolylineOptions()
                            .addAll(routePoints)
                            .width(12)
                            .color(Color.BLUE)
                            .geodesic(true));
                    Log.d(TAG, "Route drawn on map");
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Directions API error: " + error);
            }
        });
    }
    private void showSourceAndDestinationOnMap(String source, String destination) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> sourceList = geocoder.getFromLocationName(source, 1);
            List<Address> destinationList = geocoder.getFromLocationName(destination, 1);

            if (!sourceList.isEmpty() && !destinationList.isEmpty()) {
                Address sourceAddress = sourceList.get(0);
                Address destinationAddress = destinationList.get(0);

                LatLng sourceLatLng = new LatLng(sourceAddress.getLatitude(), sourceAddress.getLongitude());
                LatLng destinationLatLng = new LatLng(destinationAddress.getLatitude(), destinationAddress.getLongitude());

                googleMap.clear();

                // Add markers
                googleMap.addMarker(new MarkerOptions()
                        .position(sourceLatLng)
                        .title("Source")
                        .snippet(source));
                googleMap.addMarker(new MarkerOptions()
                        .position(destinationLatLng)
                        .title("Destination")
                        .snippet(destination));

                // Zoom map to fit both
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(sourceLatLng);
                builder.include(destinationLatLng);
                LatLngBounds bounds = builder.build();

                int padding = 150;
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                googleMap.animateCamera(cameraUpdate);
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error showing ride on map", Toast.LENGTH_SHORT).show();
        }
    }
}
