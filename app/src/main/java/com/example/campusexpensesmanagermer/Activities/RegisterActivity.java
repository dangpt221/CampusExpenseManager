package com.example.campusexpensesmanagermer.Activities;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.campusexpensesmanagermer.R;
import com.example.campusexpensesmanagermer.Repositories.UserRepository;

public class RegisterActivity extends AppCompatActivity {
    EditText edtUsername, edtPassword, edtEmail, edtPhone;
    Button btnRegister, btnBack;
    TextView tvLoginAccount;
    UserRepository userRepository;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userRepository = new UserRepository(RegisterActivity.this);
        edtUsername = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);
        edtEmail    = findViewById(R.id.edtEmail);
        edtPhone    = findViewById(R.id.edtPhone);
        btnRegister = findViewById(R.id.btnRegisterform);
        btnBack   = findViewById(R.id.btnBack);
        tvLoginAccount = findViewById(R.id.tvLoginAccount);
        /// //////////////////////////////////////////////////////
        tvLoginAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(login);
            }
        });
        // bat su kien dang ky tai khoan
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                String username = edtUsername.getText().toString().trim();
                if (TextUtils.isEmpty(username)){
                    edtUsername.setError("Username is required");
                    return;
                }
                // kiem tra username da ton tai hay chua ?
                boolean checkUsername = userRepository.checkAccountExistsByUsername(username);
                if (checkUsername){
                    edtUsername.setError("Username exists, please chose other !");
                    return;
                }

                String password = edtPassword.getText().toString().trim();
                if (TextUtils.isEmpty(password)){
                    edtPassword.setError("Password is required");
                    return;
                }
                String email = edtEmail.getText().toString().trim();
                if (TextUtils.isEmpty(email)){
                    edtEmail.setError("Email is required");
                    return;
                }
                String phoneNumber = edtPhone.getText().toString().trim();
                // insert data to database
                long insert = userRepository.saveUserAccount(username, password, email, phoneNumber);
                if (insert == -1){
                    // fail
                    Toast.makeText(RegisterActivity.this, "Register Fail", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    // success
                    Toast.makeText(RegisterActivity.this, "Register successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}