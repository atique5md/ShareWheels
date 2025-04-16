package com.example.sharewheel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class LogoutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logout, container, false);
    }

    // Perform logout logic in this method
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Delay for 2 seconds (this can be adjusted if needed)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Perform logout operations here (e.g., clear user data)
                clearSession();

                // Navigate back to the LoginActivity
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clears backstack
                startActivity(intent);
                getActivity().finish(); // Finish the current activity
            }
        }, 2000); // 2000 milliseconds = 2 seconds
    }

    // Clear user session data (SharedPreferences)
    private void clearSession() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clears all stored user data
        editor.apply();
    }
}
