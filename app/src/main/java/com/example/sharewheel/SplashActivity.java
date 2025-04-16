package com.example.sharewheel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
            String role = sharedPreferences.getString("role", "");

            if (isLoggedIn) {
                if (role.equals("Driver")) {
                    startActivity(new Intent(SplashActivity.this, DriverActivity.class));
                } else if (role.equals("Rider")) {
                    startActivity(new Intent(SplashActivity.this, RiderActivity.class));
                }
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();
        }, 2000);
    }
}
