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

import com.example.sharewheel.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private SearchView searchView;
    private List<String> allItems;
    private List<String> filteredItems;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find the CardView that wraps the SearchView
        CardView cardView; // Assuming the CardView has this ID
        cardView = view.findViewById(R.id.cardViewSearch);
        searchView = view.findViewById(R.id.searchView);

        allItems = new ArrayList<>();
        filteredItems = new ArrayList<>();

        // Add Indian state names
        allItems.add("Andhra Pradesh");
        allItems.add("Arunachal Pradesh");
        allItems.add("Assam");
        allItems.add("Bihar");
        allItems.add("Chhattisgarh");
        allItems.add("Goa");
        allItems.add("Gujarat");
        allItems.add("Haryana");
        allItems.add("Himachal Pradesh");
        allItems.add("Jharkhand");
        allItems.add("Karnataka");
        allItems.add("Kerala");
        allItems.add("Madhya Pradesh");
        allItems.add("Maharashtra");
        allItems.add("Manipur");
        allItems.add("Meghalaya");
        allItems.add("Mizoram");
        allItems.add("Nagaland");
        allItems.add("Odisha");
        allItems.add("Punjab");
        allItems.add("Rajasthan");
        allItems.add("Sikkim");
        allItems.add("Tamil Nadu");
        allItems.add("Telangana");
        allItems.add("Tripura");
        allItems.add("Uttar Pradesh");
        allItems.add("Uttarakhand");
        allItems.add("West Bengal");

        // Set search hint
        searchView.setQueryHint(getResources().getString(R.string.search_hint));

        // Set listener once for the search query
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

        // Optional: Make the entire CardView clickable
        cardView.setClickable(true);
        cardView.setOnClickListener(v -> {
            // Handle click event for CardView, if needed
            Toast.makeText(getContext(), "CardView clicked!", Toast.LENGTH_SHORT).show();
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

        // Debug log (optional)
        System.out.println("Filtered items: " + filteredItems);
    }
}
