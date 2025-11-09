package com.example.campusexpensesmanagermer.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.campusexpensesmanagermer.R;

public class LoginActivity extends AppCompatActivity {
    EditText edtUsername, edtPassword;
    Button btnLogin, btnRegister, btnForgotPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUsername = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);

        // Login button logic with debug logging
        btnLogin.setOnClickListener(v -> {
            String user = edtUsername.getText().toString().trim();
            String pass = edtPassword.getText().toString().trim();

            // Debug: Log input values
            Log.d("LoginDebug", "Username: '" + user + "', Password: '" + pass + "'");

            if (TextUtils.isEmpty(user)) {
                edtUsername.setError("Enter username, please");
                return;
            }
            if (TextUtils.isEmpty(pass)) {
                edtPassword.setError("Enter password, please");
                return;
            }

            // Authenticate user (temporarily hardcoded for testing)
            if (authenticateUser(user, pass)) {
                Log.d("LoginDebug", "Login successful, starting HomeActivity");
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("ACCOUNT", user);
                bundle.putString("EMAIL", getUserEmail(user)); // Placeholder
                bundle.putInt("ID_USER", getUserId(user));     // Placeholder
                bundle.putInt("AGE_USER", getUserAge(user));   // Placeholder
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            } else {
                Log.d("LoginDebug", "Login failed: Invalid credentials");
                Toast.makeText(LoginActivity.this, "Account invalid", Toast.LENGTH_SHORT).show();
            }
        });

        // Register button: Navigate to RegisterActivity (create this activity)
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Forgot password button: Navigate to ForgotPasswordActivity (create this activity)
        btnForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    // Placeholder method for authentication (currently hardcoded for testing)
    private boolean authenticateUser(String username, String password) {
        // Temporarily use hardcoded for debug - replace with real auth later
        return username.equals("dangpt") && password.equals("12345678");
    }

    // Placeholder methods to fetch user data (implement with DB queries later)
    private String getUserEmail(String username) {
        // Query database for email - for now, return placeholder
        return "danpt2005@gmail.com"; // Replace with real data
    }

    private int getUserId(String username) {
        // Query database for ID - for now, return placeholder
        return 100; // Replace with real data
    }

    private int getUserAge(String username) {
        // Query database for age - for now, return placeholder
        return 20; // Replace with real data
    }
}
