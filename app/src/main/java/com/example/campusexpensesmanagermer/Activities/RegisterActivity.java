package com.example.campusexpensesmanagermer.Activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.campusexpensesmanagermer.Data.SQLiteDbHelper;
import com.example.campusexpensesmanagermer.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtUserName, edtPassword, edtPhone, edtEmail;
    private Button btnRegisterform, btnBack;
    private TextView tvLoginAccount;
    private SQLiteDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        edtUserName = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        btnRegisterform = findViewById(R.id.btnRegisterform);
        btnBack = findViewById(R.id.btnBack);
        tvLoginAccount = findViewById(R.id.tvLoginAccount);

        dbHelper = new SQLiteDbHelper(this);

        // Register button click
        btnRegisterform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Back button click
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Login account text click
        tvLoginAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void registerUser() {
        String username = edtUserName.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();

        // ✅ Validate Username
        if (TextUtils.isEmpty(username)) {
            edtUserName.setError("Please enter username");
            edtUserName.requestFocus();
            return;
        }

        if (username.length() < 3) {
            edtUserName.setError("Username must be at least 3 characters");
            edtUserName.requestFocus();
            return;
        }

        // ✅ Validate Password - Minimum 8 characters
        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Please enter password");
            edtPassword.requestFocus();
            return;
        }

        if (password.length() < 8) {
            edtPassword.setError("Password must be at least 8 characters");
            edtPassword.requestFocus();
            Toast.makeText(this, "❌ Password must be at least 8 characters!", Toast.LENGTH_LONG).show();
            return;
        }

        // ✅ Validate Phone - Exactly 10 digits
        if (TextUtils.isEmpty(phone)) {
            edtPhone.setError("Please enter phone number");
            edtPhone.requestFocus();
            return;
        }

        if (!phone.matches("\\d{10}")) {
            edtPhone.setError("Phone number must be exactly 10 digits");
            edtPhone.requestFocus();
            Toast.makeText(this, "❌ Phone number must be exactly 10 digits!", Toast.LENGTH_LONG).show();
            return;
        }

        // ✅ Validate Email
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Please enter email");
            edtEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Please enter valid email");
            edtEmail.requestFocus();
            return;
        }

        // Insert into database
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQLiteDbHelper.USERNAME_USER, username);
        values.put(SQLiteDbHelper.PASSWORD_USER, password);
        values.put(SQLiteDbHelper.PHONE_USER, phone);
        values.put(SQLiteDbHelper.EMAIL_USER, email);
        values.put(SQLiteDbHelper.ROLE_USER, 0);
        values.put(SQLiteDbHelper.STATUS_USER, 1);

        try {
            long result = db.insert(SQLiteDbHelper.TABLE_USER, null, values);

            if (result != -1) {
                Toast.makeText(this, "✅ Registration successful!", Toast.LENGTH_SHORT).show();

                // Clear fields
                edtUserName.setText("");
                edtPassword.setText("");
                edtPhone.setText("");
                edtEmail.setText("");

                // Go to login
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "❌ Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "❌ Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}