package com.example.campusexpensesmanagermer.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

// Import các thư viện giao diện
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.switchmaterial.SwitchMaterial;

// ⚠️ QUAN TRỌNG: Nếu dòng dưới đây báo đỏ, hãy xóa đi và gõ lại "import com.example." rồi chọn gợi ý có chữ R
import com.example.campusexpensesmanagermer.R;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_setting);

        // Khởi tạo bộ nhớ
        sharedPreferences = getSharedPreferences("UserSettings", Context.MODE_PRIVATE);

        // Gọi các hàm xử lý tách biệt
        setupHeader();
        setupDisplaySection();
        setupNotificationSection();
        setupSecuritySection();
        setupLogout();
    }

    // --- 1. Xử lý Nút Quay Lại ---
    private void setupHeader() {
        Button btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    // --- 2. Xử lý Giao diện (Dark Mode & Ngôn ngữ) ---
    private void setupDisplaySection() {
        // Dark Mode
        SwitchMaterial switchDarkMode = findViewById(R.id.switchDarkMode);
        boolean isDarkMode = sharedPreferences.getBoolean("DARK_MODE", false);
        if (switchDarkMode != null) {
            switchDarkMode.setChecked(isDarkMode);
            switchDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    sharedPreferences.edit().putBoolean("DARK_MODE", isChecked).apply();
                    if (isChecked) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                }
            });
        }

        // Ngôn ngữ
        LinearLayout layoutLanguage = findViewById(R.id.layoutLanguage);
        final TextView tvCurrentLang = findViewById(R.id.tvCurrentLang);
        if (layoutLanguage != null) {
            layoutLanguage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String[] languages = {"Tiếng Việt", "English",};
                    new AlertDialog.Builder(SettingsActivity.this)
                            .setTitle("Chọn ngôn ngữ")
                            .setItems(languages, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (tvCurrentLang != null) {
                                        tvCurrentLang.setText(languages[which]);
                                    }
                                    Toast.makeText(SettingsActivity.this, "Đã chọn: " + languages[which], Toast.LENGTH_SHORT).show();
                                }
                            })
                            .show();
                }
            });
        }
    }

    // --- 3. Xử lý Thông báo ---
    private void setupNotificationSection() {
        SwitchMaterial switchSpending = findViewById(R.id.switchNotiSpending);
        SwitchMaterial switchLimit = findViewById(R.id.switchNotiLimit);

        if (switchSpending != null) {
            switchSpending.setChecked(sharedPreferences.getBoolean("NOTI_SPENDING", true));
            switchSpending.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    sharedPreferences.edit().putBoolean("NOTI_SPENDING", isChecked).apply();
                }
            });
        }

        if (switchLimit != null) {
            switchLimit.setChecked(sharedPreferences.getBoolean("NOTI_LIMIT", true));
            switchLimit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    sharedPreferences.edit().putBoolean("NOTI_LIMIT", isChecked).apply();
                }
            });
        }
    }

    // --- 4. Xử lý Bảo mật ---
    private void setupSecuritySection() {
        LinearLayout layoutSecurity = findViewById(R.id.layoutSecurity);
        LinearLayout layoutBackup = findViewById(R.id.layoutBackup);

        if (layoutSecurity != null) {
            layoutSecurity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(SettingsActivity.this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (layoutBackup != null) {
            layoutBackup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(SettingsActivity.this, "Đang sao lưu...", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // --- 5. Xử lý Đăng xuất ---
    private void setupLogout() {
        Button btnLogout = findViewById(R.id.btnLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(SettingsActivity.this)
                            .setTitle("Đăng xuất")
                            .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                            .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(SettingsActivity.this, "Đã đăng xuất!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            })
                            .setNegativeButton("Hủy", null)
                            .show();
                }
            });
        }
    }
}