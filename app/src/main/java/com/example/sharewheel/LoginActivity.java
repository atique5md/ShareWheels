package com.example.sharewheel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText usernameInput, passwordInput;
    Button loginBtn;
    RadioGroup userTypeGroup;
    RadioButton driverRadio, riderRadio;
    TextView signupRedirect;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        loginBtn = findViewById(R.id.login_btn);
        userTypeGroup = findViewById(R.id.user_type_group);
        driverRadio = findViewById(R.id.driver_radio);
        riderRadio = findViewById(R.id.rider_radio);
        signupRedirect = findViewById(R.id.signup_redirect);

        // Initially disable the login button until a role is selected (optional)
        // loginBtn.setEnabled(false);

        // Listen for radio group selection changes
        userTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.driver_radio) {
                loginBtn.setBackgroundColor(getResources().getColor(R.color.driver_color)); // Replace with your color
                loginBtn.setText("Login as Driver");
                loginBtn.setVisibility(Button.VISIBLE); // Optional
            } else if (checkedId == R.id.rider_radio) {
                loginBtn.setBackgroundColor(getResources().getColor(R.color.rider_color)); // Replace with your color
                loginBtn.setText("Login as Rider");
                loginBtn.setVisibility(Button.VISIBLE); // Optional
            }
        });

        loginBtn.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            int selectedRoleId = userTypeGroup.getCheckedRadioButtonId();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (username.length() < 6) {
                Toast.makeText(this, "Username must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidPassword(password)) {
                Toast.makeText(this, "Password must contain at least one digit and one special character", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedRoleId == -1) {
                Toast.makeText(this, "Please select a role (Driver or Rider)", Toast.LENGTH_SHORT).show();
                return;
            }

            // Retrieve stored data
            SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
            String storedUsername = sharedPreferences.getString("username", null);
            String storedPassword = sharedPreferences.getString("password", null);
            String storedRole = sharedPreferences.getString("role", null);

            String selectedRole = (selectedRoleId == driverRadio.getId()) ? "Driver" : "Rider";

            if (storedUsername == null || storedPassword == null || storedRole == null) {
                Toast.makeText(this, "No user found. Please sign up first.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (username.equals(storedUsername) && password.equals(storedPassword) && selectedRole.equals(storedRole)) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

                if (selectedRole.equals("Driver")) {
                    startActivity(new Intent(this, DriverActivity.class));
                } else {
                    startActivity(new Intent(this, RiderActivity.class));
                }
                finish();
            } else {
                Toast.makeText(this, "Invalid credentials or role. Try again.", Toast.LENGTH_SHORT).show();
            }
        });

        signupRedirect.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });
    }

    private boolean isValidPassword(String password) {
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) hasDigit = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecialChar = true;
        }
        return hasDigit && hasSpecialChar;
    }
}
