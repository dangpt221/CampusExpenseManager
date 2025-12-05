package com.example.campusexpensesmanagermer.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.campusexpensesmanagermer.Activities.LoginActivity;
import com.example.campusexpensesmanagermer.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private static final String PREFS_NAME = "CampusExpensesPrefs";
    private static final String SETTINGS_PREFS = "AppSettings";

    private SharedPreferences prefs;
    private SharedPreferences settingsPrefs;

    // Views
    private SwitchMaterial switchDarkMode;
    private SwitchMaterial switchSpendingReminder;
    private SwitchMaterial switchLimitReminder;
    private TextView tvCurrency;
    private TextView tvLanguage;
    private TextView tvSecurity;
    private TextView tvBackupRestore;
    private TextView tvLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_setting);

        // Initialize SharedPreferences
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        settingsPrefs = getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE);

        // Initialize views
        initViews();

        // Setup sections
        setupDisplaySection();
        setupNotificationSection();
        setupSecuritySection();
        setupLogout();
    }

    private void initViews() {
        switchDarkMode = findViewById(R.id.switch_dark_mode);
        switchSpendingReminder = findViewById(R.id.switch_spending_reminder);
        switchLimitReminder = findViewById(R.id.switch_limit_reminder);
        tvCurrency = findViewById(R.id.tv_currency);
        tvLanguage = findViewById(R.id.tv_language);
        tvSecurity = findViewById(R.id.tv_security);
        tvBackupRestore = findViewById(R.id.tv_backup_restore);
        tvLogout = findViewById(R.id.tv_logout);
    }

    // --- 2. Xử lý Giao diện (Dark Mode & Ngôn ngữ) ---
    private void setupDisplaySection() {
        // Load and setup Dark Mode
        if (switchDarkMode != null) {
            boolean isDarkMode = settingsPrefs.getBoolean("dark_mode", false);
            switchDarkMode.setChecked(isDarkMode);
            switchDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    settingsPrefs.edit().putBoolean("dark_mode", isChecked).apply();
                    if (isChecked) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        Toast.makeText(SettingsActivity.this, "Đã bật Dark Mode", Toast.LENGTH_SHORT).show();
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        Toast.makeText(SettingsActivity.this, "Đã tắt Dark Mode", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // Setup Currency
        if (tvCurrency != null) {
            String currency = settingsPrefs.getString("currency", "VND");
            tvCurrency.setText("Tiền tệ: " + currency);
            tvCurrency.setOnClickListener(v -> showCurrencyDialog());
        }

        // Setup Language
        if (tvLanguage != null) {
            String language = settingsPrefs.getString("language", "Tiếng Việt");
            tvLanguage.setText("Ngôn ngữ: " + language);
            tvLanguage.setOnClickListener(v -> showLanguageDialog());
        }
    }

    // --- 3. Xử lý Thông báo ---
    private void setupNotificationSection() {
        if (switchSpendingReminder != null) {
            boolean spendingReminder = settingsPrefs.getBoolean("spending_reminder", true);
            switchSpendingReminder.setChecked(spendingReminder);
            switchSpendingReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    settingsPrefs.edit().putBoolean("spending_reminder", isChecked).apply();
                    String message = isChecked ? "Đã bật nhắc nhở chi tiêu" : "Đã tắt nhắc nhở chi tiêu";
                    Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (switchLimitReminder != null) {
            boolean limitReminder = settingsPrefs.getBoolean("limit_reminder", true);
            switchLimitReminder.setChecked(limitReminder);
            switchLimitReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    settingsPrefs.edit().putBoolean("limit_reminder", isChecked).apply();
                    String message = isChecked ? "Đã bật nhắc nhở giới hạn" : "Đã tắt nhắc nhở giới hạn";
                    Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // --- 4. Xử lý Bảo mật ---
    private void setupSecuritySection() {
        if (tvSecurity != null) {
            tvSecurity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(SettingsActivity.this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (tvBackupRestore != null) {
            tvBackupRestore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showBackupRestoreDialog();
                }
            });
        }
    }

    // --- 5. Xử lý Đăng xuất ---
    private void setupLogout() {
        if (tvLogout != null) {
            tvLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLogoutDialog();
                }
            });
        }
    }

    private void showCurrencyDialog() {
        String[] currencies = {"VND", "USD", "EUR", "JPY"};
        String currentCurrency = settingsPrefs.getString("currency", "VND");
        int selectedIndex = 0;

        for (int i = 0; i < currencies.length; i++) {
            if (currencies[i].equals(currentCurrency)) {
                selectedIndex = i;
                break;
            }
        }

        new AlertDialog.Builder(SettingsActivity.this)
                .setTitle("Chọn tiền tệ")
                .setSingleChoiceItems(currencies, selectedIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selected = currencies[which];
                        settingsPrefs.edit().putString("currency", selected).apply();
                        if (tvCurrency != null) {
                            tvCurrency.setText("Tiền tệ: " + selected);
                        }
                        Toast.makeText(SettingsActivity.this, "Đã đổi sang " + selected, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showLanguageDialog() {
        String[] languages = {"Tiếng Việt", "English"};
        String currentLanguage = settingsPrefs.getString("language", "Tiếng Việt");
        int selectedIndex = 0;

        for (int i = 0; i < languages.length; i++) {
            if (languages[i].equals(currentLanguage)) {
                selectedIndex = i;
                break;
            }
        }

        new AlertDialog.Builder(SettingsActivity.this)
                .setTitle("Chọn ngôn ngữ")
                .setSingleChoiceItems(languages, selectedIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selected = languages[which];
                        settingsPrefs.edit().putString("language", selected).apply();
                        if (tvLanguage != null) {
                            tvLanguage.setText("Ngôn ngữ: " + selected);
                        }
                        Toast.makeText(SettingsActivity.this, "Đã đổi sang " + selected, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showBackupRestoreDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle("Sao lưu & Khôi phục");

        String[] options = {"Sao lưu dữ liệu", "Khôi phục dữ liệu"};
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Toast.makeText(SettingsActivity.this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(SettingsActivity.this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        performLogout();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void performLogout() {
        // Clear user session
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        // Navigate to Login screen
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        Toast.makeText(SettingsActivity.this, "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show();
    }
}
