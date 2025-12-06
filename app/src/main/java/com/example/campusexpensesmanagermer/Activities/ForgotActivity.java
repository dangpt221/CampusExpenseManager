package com.example.campusexpensesmanagermer.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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

import java.util.regex.Pattern;

public class ForgotActivity extends AppCompatActivity {
    // Đổi edtPhone thành edtReEnterPassword
    EditText edtUsername, edtPassword, edtReEnterPassword, edtEmail;
    Button btnConfirm, btnBack;
    TextView tvLoginAccount;
    UserRepository userRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        userRepository = new UserRepository(ForgotActivity.this);

        // Ánh xạ View
        edtUsername = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);
        edtReEnterPassword = findViewById(R.id.edtReEnterPassword); // ID mới từ XML
        edtEmail    = findViewById(R.id.edtEmail);
        btnConfirm  = findViewById(R.id.btnConfirm);
        btnBack     = findViewById(R.id.btnBack);
        tvLoginAccount = findViewById(R.id.tvLoginAccount);

        // Chuyển sang màn hình Login
        tvLoginAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent(ForgotActivity.this, LoginActivity.class);
                startActivity(login);
                finish();
            }
        });

        // Sự kiện nút Back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Bắt sự kiện xác nhận đổi mật khẩu
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                String username = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String reEnterPassword = edtReEnterPassword.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();

                // 1. Kiểm tra để trống
                if (TextUtils.isEmpty(username)){
                    edtUsername.setError("Username is required");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    edtPassword.setError("New Password is required");
                    return;
                }
                if (TextUtils.isEmpty(reEnterPassword)){
                    edtReEnterPassword.setError("Please re-enter password");
                    return;
                }
                if (TextUtils.isEmpty(email)){
                    edtEmail.setError("Email is required");
                    return;
                }

                // 2. Kiểm tra định dạng Email (Tùy chọn thêm)
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    edtEmail.setError("Invalid email format");
                    return;
                }

                // 3. Kiểm tra mật khẩu nhập lại có khớp không
                if (!password.equals(reEnterPassword)) {
                    edtReEnterPassword.setError("Passwords do not match");
                    return;
                }

                // 4. Kiểm tra độ mạnh của mật khẩu
                if (!isStrongPassword(password)) {
                    edtPassword.setError("Password must be at least 6 characters, contain at least 1 number and 1 letter.");
                    return;
                }

                // 5. Kiểm tra Username và Email có tồn tại và khớp nhau không?
                // Lưu ý: Bạn cần thêm hàm verifyUserAndEmail vào UserRepository
                boolean isUserValid = userRepository.verifyUserAndEmail(username, email);

                if (!isUserValid){
                    Toast.makeText(ForgotActivity.this, "Username and Email do not match or account not found!", Toast.LENGTH_LONG).show();
                    return;
                }

                // 6. Thực hiện cập nhật mật khẩu
                boolean isUpdated = userRepository.updatePassword(username, password);

                if (isUpdated){
                    Toast.makeText(ForgotActivity.this, "Password reset successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ForgotActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ForgotActivity.this, "Reset failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Hàm kiểm tra độ mạnh mật khẩu
    // Quy tắc: Ít nhất 6 ký tự, có ít nhất 1 chữ cái và 1 số
    private boolean isStrongPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        // Regex: Chứa ít nhất 1 chữ (a-zA-Z) và 1 số (0-9)
        Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z]).{6,}$");
        return pattern.matcher(password).matches();
    }
}