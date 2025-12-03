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

public class ForgotActivity extends AppCompatActivity {
    EditText edtUsername, edtPassword, edtEmail, edtPhone;
    Button btnConfirm, btnBack;
    TextView tvLoginAccount;
    UserRepository userRepository;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        userRepository = new UserRepository(ForgotActivity.this);
        edtUsername = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);
        edtEmail    = findViewById(R.id.edtEmail);
        edtPhone    = findViewById(R.id.edtPhone);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnBack   = findViewById(R.id.btnBack);
        tvLoginAccount = findViewById(R.id.tvLoginAccount);
        /// //////////////////////////////////////////////////////
        tvLoginAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent(ForgotActivity.this, LoginActivity.class);
                startActivity(login);
            }
        });

        // Bắt sự kiện xác nhận đổi mật khẩu
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                String username = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String phoneNumber = edtPhone.getText().toString().trim();
                if (TextUtils.isEmpty(username)){
                    edtUsername.setError("Username is required");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    edtPassword.setError("New Password is required");
                    return;
                }

                // Kiểm tra xem tài khoản có tồn tại không?
                boolean checkUsername = userRepository.checkAccountExistsByUsername(username);
                // Nếu không tồn tại thì báo lỗi
                if (!checkUsername){
                    edtUsername.setError("Username does not exist!");
                    return;
                }

                // Thực hiện cập nhật mật khẩu
                boolean isUpdated = userRepository.updatePassword(username, password);

                if (isUpdated){
                    Toast.makeText(ForgotActivity.this, "Password reset successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ForgotActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Fail
                    Toast.makeText(ForgotActivity.this, "Reset failed. Please try again.", Toast.LENGTH_SHORT).show();
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