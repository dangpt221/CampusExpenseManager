package com.example.campusexpensesmanagermer.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.campusexpensesmanagermer.Activities.LoginActivity;
import com.example.campusexpensesmanagermer.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingFragment extends Fragment {

    private static final String TAG = "SettingFragment";
    private static final String PREFS_NAME = "CampusExpensesPrefs";
    private static final String SETTINGS_PREFS = "AppSettings";

    // Views
    private SwitchMaterial switchDarkMode;
    private SwitchMaterial switchSpendingReminder;
    private SwitchMaterial switchLimitReminder;
    private TextView tvCurrency;
    private TextView tvLanguage;
    private TextView tvSecurity;
    private TextView tvBackupRestore;
    private TextView tvLogout;

    // SharedPreferences
    private SharedPreferences prefs;
    private SharedPreferences settingsPrefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        initPreferences();
        loadSettings();
        setupListeners();
    }

    private void initViews(View view) {
        switchDarkMode = view.findViewById(R.id.switch_dark_mode);
        switchSpendingReminder = view.findViewById(R.id.switch_spending_reminder);
        switchLimitReminder = view.findViewById(R.id.switch_limit_reminder);
        tvCurrency = view.findViewById(R.id.tv_currency);
        tvLanguage = view.findViewById(R.id.tv_language);
        tvSecurity = view.findViewById(R.id.tv_security);
        tvBackupRestore = view.findViewById(R.id.tv_backup_restore);
        tvLogout = view.findViewById(R.id.tv_logout);
    }

    private void initPreferences() {
        prefs = requireActivity().getSharedPreferences(PREFS_NAME, requireContext().MODE_PRIVATE);
        settingsPrefs = requireActivity().getSharedPreferences(SETTINGS_PREFS, requireContext().MODE_PRIVATE);
    }

    private void loadSettings() {
        try {
            // Load Dark Mode setting
            if (switchDarkMode != null) {
                boolean isDarkMode = settingsPrefs.getBoolean("dark_mode", false);
                switchDarkMode.setChecked(isDarkMode);
            }

            // Load notification settings
            if (switchSpendingReminder != null) {
                boolean spendingReminder = settingsPrefs.getBoolean("spending_reminder", true);
                switchSpendingReminder.setChecked(spendingReminder);
            }
            
            if (switchLimitReminder != null) {
                boolean limitReminder = settingsPrefs.getBoolean("limit_reminder", true);
                switchLimitReminder.setChecked(limitReminder);
            }

            // Load currency and language
            String currency = settingsPrefs.getString("currency", "VND");
            String language = settingsPrefs.getString("language", "Tiếng Việt");
            
            if (tvCurrency != null) {
                tvCurrency.setText("Tiền tệ: " + currency);
            }
            if (tvLanguage != null) {
                tvLanguage.setText("Ngôn ngữ: " + language);
            }

            Log.d(TAG, "Settings loaded successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error loading settings: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupListeners() {
        // Dark Mode Switch
        if (switchDarkMode != null) {
            switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
                settingsPrefs.edit().putBoolean("dark_mode", isChecked).apply();

                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    Toast.makeText(requireContext(), "Đã bật Dark Mode", Toast.LENGTH_SHORT).show();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    Toast.makeText(requireContext(), "Đã tắt Dark Mode", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Spending Reminder Switch
        if (switchSpendingReminder != null) {
            switchSpendingReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
                settingsPrefs.edit().putBoolean("spending_reminder", isChecked).apply();
                String message = isChecked ? "Đã bật nhắc nhở chi tiêu" : "Đã tắt nhắc nhở chi tiêu";
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            });
        }

        // Limit Reminder Switch
        if (switchLimitReminder != null) {
            switchLimitReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
                settingsPrefs.edit().putBoolean("limit_reminder", isChecked).apply();
                String message = isChecked ? "Đã bật nhắc nhở giới hạn" : "Đã tắt nhắc nhở giới hạn";
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            });
        }

        // Currency Selection
        if (tvCurrency != null) {
            tvCurrency.setOnClickListener(v -> showCurrencyDialog());
        }

        // Language Selection
        if (tvLanguage != null) {
            tvLanguage.setOnClickListener(v -> showLanguageDialog());
        }

        // Security Settings
        if (tvSecurity != null) {
            tvSecurity.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
            });
        }

        // Backup & Restore
        if (tvBackupRestore != null) {
            tvBackupRestore.setOnClickListener(v -> showBackupRestoreDialog());
        }

        // Logout
        if (tvLogout != null) {
            tvLogout.setOnClickListener(v -> showLogoutDialog());
        }
    }

    private void showCurrencyDialog() {
        try {
            String[] currencies = {"VND", "USD", "EUR", "JPY"};
            String currentCurrency = settingsPrefs.getString("currency", "VND");
            int selectedIndex = 0;

            for (int i = 0; i < currencies.length; i++) {
                if (currencies[i].equals(currentCurrency)) {
                    selectedIndex = i;
                    break;
                }
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Chọn tiền tệ");
            builder.setSingleChoiceItems(currencies, selectedIndex, (dialog, which) -> {
                String selected = currencies[which];
                settingsPrefs.edit().putString("currency", selected).apply();
                if (tvCurrency != null) {
                    tvCurrency.setText("Tiền tệ: " + selected);
                }
                Toast.makeText(requireContext(), "Đã đổi sang " + selected, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
            builder.setNegativeButton("Hủy", null);
            builder.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing currency dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showLanguageDialog() {
        try {
            String[] languages = {"Tiếng Việt", "English"};
            String currentLanguage = settingsPrefs.getString("language", "Tiếng Việt");
            int selectedIndex = 0;

            for (int i = 0; i < languages.length; i++) {
                if (languages[i].equals(currentLanguage)) {
                    selectedIndex = i;
                    break;
                }
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Chọn ngôn ngữ");
            builder.setSingleChoiceItems(languages, selectedIndex, (dialog, which) -> {
                String selected = languages[which];
                settingsPrefs.edit().putString("language", selected).apply();
                if (tvLanguage != null) {
                    tvLanguage.setText("Ngôn ngữ: " + selected);
                }
                Toast.makeText(requireContext(), "Đã đổi sang " + selected, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
            builder.setNegativeButton("Hủy", null);
            builder.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing language dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showBackupRestoreDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Sao lưu & Khôi phục");

            String[] options = {"Sao lưu dữ liệu", "Khôi phục dữ liệu"};
            builder.setItems(options, (dialog, which) -> {
                if (which == 0) {
                    // Backup data
                    Toast.makeText(requireContext(), "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
                } else {
                    // Restore data
                    Toast.makeText(requireContext(), "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Hủy", null);
            builder.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing backup/restore dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showLogoutDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Đăng xuất");
            builder.setMessage("Bạn có chắc chắn muốn đăng xuất?");

            builder.setPositiveButton("Đồng ý", (dialog, which) -> {
                performLogout();
            });

            builder.setNegativeButton("Hủy", null);
            builder.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing logout dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void performLogout() {
        try {
            // Clear user session
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Log.d(TAG, "User logged out");

            // Navigate to Login screen
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();

            Toast.makeText(requireContext(), "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error during logout: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(requireContext(), "Lỗi khi đăng xuất", Toast.LENGTH_SHORT).show();
        }
    }
}
