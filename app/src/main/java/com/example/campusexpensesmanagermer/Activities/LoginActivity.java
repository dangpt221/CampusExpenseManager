package com.example.campusexpensesmanagermer.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
    TextView tvRegister;
    UserRepository userRepository;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userRepository = new UserRepository(LoginActivity.this);
        // anh xa view
        edtUsername = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);
        btnCancel = findViewById(R.id.btnCancel);
        btnLogin  = findViewById(R.id.btnLogin);
        tvRegister= findViewById(R.id.tvRegisterAccount);
        // bat su kien nguoi chuyen chuyen sang dang ky tai khoan
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent register = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(register);
            }
        });
        // bat su kien - khi nguoi dung bam vao button Login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = edtUsername.getText().toString().trim();
                String pass = edtPassword.getText().toString().trim();
                if (TextUtils.isEmpty(user)){
                    edtUsername.setError("Enter username, please");
                    return;
                }
                if (TextUtils.isEmpty(pass)){
                    edtPassword.setError("Enter password, please");
                    return;
                }
                // login user with database
                Users userInfo = userRepository.getInfoUser(user, pass);
                assert userInfo != null;
                if (userInfo.getUsername() != null && userInfo.getId() > 0){
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("ACCOUNT", userInfo.getUsername());
                    bundle.putString("EMAIL", userInfo.getEmail());
                    bundle.putInt("ID_USER", userInfo.getId());
                    bundle.putInt("ROLE_USER", userInfo.getRole());
                    intent.putExtras(bundle);
                    startActivity(intent); // chuyen sang man hinh khac
                    finish(); // khong cho phep nguoi dung bam quay lai giao dien dang nhap
                } else {
                    Toast.makeText(LoginActivity.this, "Account invalid", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
