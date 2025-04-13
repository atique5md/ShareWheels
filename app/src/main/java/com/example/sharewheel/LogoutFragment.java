package com.example.sharewheel;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.sharewheel.LoginActivity;
import com.example.sharewheel.R;

public class LogoutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logout, container, false);
    }
    //perform logout logic in this method
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Delay for 2 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Perform logout operations here (e.g., clear user data)
                // Navigate back to the LoginActivity or another appropriate screen
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }, 20); // 2000 milliseconds = 2 seconds
    }
}