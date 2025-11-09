package com.example.campusexpensesmanagermer.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.campusexpensesmanagermer.R;

public class LoginActivity extends AppCompatActivity {
    EditText edtUsername, edtPassword;
    Button btnLogin, btnRegister, btnforgotpassword;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_login);
        //
        edtUsername = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnforgotpassword = findViewById(R.id.btnForgotPassword);
        // bat su kien - khi nguoi dung bam vao button Login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = edtUsername.getText().toString().trim();
                String pass = edtPassword.getText().toString().trim();
                if(TextUtils.isEmpty(user)){
                    edtUsername.setError("Enter username, please");
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    edtPassword.setError("Enter password, please");
                    return;
                }
                if(user.equals("dangpt") && pass.equals("12345678")){
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    // send data tu LoginAcitvity sang MainActivity
                    Bundle bundle = new Bundle();
                    bundle.putString("ACCOUNT", "dangpt");
                    bundle.putString("EMAIL", "danpt2005@gmail.com");
                    bundle.putInt("ID_USER", 100);
                    bundle.putInt("AGE_USER", 20);
                    intent.putExtras(bundle);
                    startActivity(intent);

                    startActivity(intent); // chuyen sang man hinh khac
                    finish(); // khong cho phep nguoi dung bam quay lai dao dien dang nhap

                }else{
                    Toast.makeText(LoginActivity.this, "Account invalid", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
