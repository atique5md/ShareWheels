package com.example.sharewheel;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

public class DriverActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected( MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    loadFragment(new HomeFragment());
                } else if (id == R.id.nav_profile) {
                    // Uncomment when ProfileFragment is ready
                     loadFragment(new ProfileFragment());
                    Toast.makeText(DriverActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_setting) {
                    // Uncomment when HelpFragment is ready
                     loadFragment(new SettingFragment());
                    Toast.makeText(DriverActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_logout) {
                    Toast.makeText(DriverActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                    loadFragment(new LogoutFragment());

                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // Load default fragment
        loadFragment(new HomeFragment());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
    private void performLogout() {
        // Perform any necessary logout operations here
        // (e.g., clear user data, authentication tokens, etc.)

        // After logout, navigate to the LoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
