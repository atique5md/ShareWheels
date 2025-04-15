package com.example.sharewheel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat; // ✅ Import this

public class SignUpActivity extends AppCompatActivity {

    EditText usernameInput, passwordInput;
    Button signupBtn;
    RadioGroup userTypeGroup;
    RadioButton driverRadio, riderRadio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        signupBtn = findViewById(R.id.signup_btn);
        userTypeGroup = findViewById(R.id.user_type_group);
        driverRadio = findViewById(R.id.driver_radio);
        riderRadio = findViewById(R.id.rider_radio);
        TextView loginRedirect = findViewById(R.id.login_redirect);

        // ✅ Change button color based on role selection
        userTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == driverRadio.getId()) {
                signupBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.driver_color));
            } else if (checkedId == riderRadio.getId()) {
                signupBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.rider_color));
            }
        });

        signupBtn.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            int selectedRoleId = userTypeGroup.getCheckedRadioButtonId();

            if (username.isEmpty() || password.isEmpty() || selectedRoleId == -1) {
                Toast.makeText(this, "Please fill all fields and select a role", Toast.LENGTH_SHORT).show();
            } else {
                String role = (selectedRoleId == driverRadio.getId()) ? "Driver" : "Rider";

                SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username", username);
                editor.putString("password", password);
                editor.putString("role", role);
                editor.apply();

                Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        });

        loginRedirect.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });
    }
}
