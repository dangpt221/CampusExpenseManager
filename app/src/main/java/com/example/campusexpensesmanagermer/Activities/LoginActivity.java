package com.example.campusexpensesmanagermer.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.campusexpensesmanagermer.Models.Users;
import com.example.campusexpensesmanagermer.R;
import com.example.campusexpensesmanagermer.Repositories.UserRepository;

public class LoginActivity extends AppCompatActivity {
    EditText edtUsername, edtPassword;
    Button btnLogin, btnCancel;
    TextView tvRegister, tvForgetpassword;
    UserRepository userRepository;

    private static final String PREFS_NAME = "CampusExpensesPrefs";
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userRepository = new UserRepository(LoginActivity.this);

        // anh xa view
        edtUsername = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);
        btnCancel = findViewById(R.id.btnCancel);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegisterAccount);
        tvForgetpassword = findViewById(R.id.tvForgotpassword);

        // bat su kien nguoi chuyen chuyen sang dang ky tai khoan
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent register = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(register);
            }
        });

        // bat su kien doi mat khau
        tvForgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forget = new Intent(LoginActivity.this, ForgotActivity.class);
                startActivity(forget);
            }
        });

        // bat su kien - khi nguoi dung bam vao button Login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = edtUsername.getText().toString().trim();
                String pass = edtPassword.getText().toString().trim();

                if (TextUtils.isEmpty(user)) {
                    edtUsername.setError("Enter username, please");
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    edtPassword.setError("Enter password, please");
                    return;
                }

                // login user with database
                Users userInfo = userRepository.getInfoUser(user, pass);

                if (userInfo != null && userInfo.getUsername() != null && userInfo.getId() > 0) {
                    Log.d(TAG, "Login successful. UserId: " + userInfo.getId() + ", Username: " + userInfo.getUsername());

                    // Lưu thông tin vào SharedPreferences
                    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putInt("ID_USER", userInfo.getId());
                    editor.putString("username", userInfo.getUsername());
                    editor.putString("email", userInfo.getEmail());
                    editor.putInt("role", userInfo.getRole());
                    editor.apply();

                    Log.d(TAG, "Saved to SharedPreferences - ID_USER: " + userInfo.getId());

                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("ACCOUNT", userInfo.getUsername());
                    bundle.putString("EMAIL", userInfo.getEmail());
                    bundle.putInt("ID_USER", userInfo.getId());
                    bundle.putInt("ROLE_USER", userInfo.getRole());
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e(TAG, "Login failed. Invalid credentials.");
                    Toast.makeText(LoginActivity.this, "Account invalid", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // bat su kien khi nguoi dung bam vao button Cancel
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}