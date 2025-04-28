package com.example.sharewheel;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeDriverFragment extends Fragment {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private StateAdapter adapter;
    private List<String> allItems;
    private List<String> filteredItems;

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
        recyclerView = view.findViewById(R.id.recyclerView);

        allItems = new ArrayList<>();
        filteredItems = new ArrayList<>();

        // ✅ Add Indian state names (do NOT redeclare allItems here)
        Collections.addAll(allItems,
                "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa",
                "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand", "Karnataka", "Kerala",
                "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland",
                "Odisha", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana",
                "Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal"
        );

        filteredItems.addAll(allItems); // Initial load

        // ✅ Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new StateAdapter(filteredItems);
        recyclerView.setAdapter(adapter);

        searchView.setQueryHint("Search for a state");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterItems(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterItems(newText);
                return true;
            }
        });

        // Optional: CardView click
        cardView.setOnClickListener(v -> {
            if (getContext() != null)
                Toast.makeText(getContext(), "CardView clicked!", Toast.LENGTH_SHORT).show();
        });
        // Optional: SearchView focus change
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false); // Expand the search view
            }
        });

        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            recyclerView.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
        });

    // Add Map Fragment inside map_container
    SupportMapFragment mapFragment = SupportMapFragment.newInstance();
    getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.map_container, mapFragment)
                .commit();

        mapFragment.getMapAsync(new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            googleMap.getUiSettings().setZoomControlsEnabled(true);

            // Move to India's center
            LatLng india = new LatLng(30.2689, 77.9931);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(india, 4));
        }
    });
}

    private void filterItems(String query) {
        filteredItems.clear();

        if (query.isEmpty()) {
            filteredItems.addAll(allItems);
        } else {
            for (String item : allItems) {
                if (item.toLowerCase().contains(query.toLowerCase())) {
                    filteredItems.add(item);
                }
            }
        }

        if (filteredItems.isEmpty()) {
            Toast.makeText(getContext(), "No results found", Toast.LENGTH_SHORT).show();
        }

        adapter.updateList(new ArrayList<>(filteredItems)); // ✅ Update the adapter
    }
}
