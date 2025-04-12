package com.example.sharewheel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    EditText username_input;
    EditText password_input;
    Button login_btn;
    CheckBox diverBox, riderBox;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        username_input = findViewById(R.id.username_input);
        password_input = findViewById(R.id.password_input);
        login_btn = findViewById(R.id.login_btn);
        diverBox = findViewById(R.id.Driver_box);
        riderBox = findViewById(R.id.Rider_box);

        // Make checkboxes mutually exclusive
        diverBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                riderBox.setChecked(false);
            }
        });

        riderBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                diverBox.setChecked(false);
            }
        });

        // Handle login logic
        login_btn.setOnClickListener(v -> {
            String username = username_input.getText().toString().trim();
            String password = password_input.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            } else if (username.length() < 6) {
                Toast.makeText(LoginActivity.this, "Username must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(LoginActivity.this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            } else if (!isValidPassword(password)) {
                Toast.makeText(LoginActivity.this, "Password must contain at least one digit and one special character", Toast.LENGTH_SHORT).show();
            } else if (!diverBox.isChecked() && !riderBox.isChecked()) {
                Toast.makeText(LoginActivity.this, "Please select a role (Driver or Rider)", Toast.LENGTH_SHORT).show();
            } else {
                // Redirect based on role
                if (diverBox.isChecked()) {
                    startActivity(new Intent(this, DriverActivity.class));
                } else if (riderBox.isChecked()) {
                    startActivity(new Intent(this, RiderActivity.class));
                }
                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Validate password for digit + special character
    private boolean isValidPassword(String password) {
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecialChar = true;
            }
        }

        return hasDigit && hasSpecialChar;
    }
}
