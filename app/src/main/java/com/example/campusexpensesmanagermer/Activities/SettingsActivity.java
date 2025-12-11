package com.example.campusexpensesmanagermer.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.campusexpensesmanagermer.Activities.LoginActivity;
import com.example.campusexpensesmanagermer.R;
import com.example.campusexpensesmanagermer.Utils.LanguageUtils;
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
        tvSecurity = findViewById(R.id.tv_security);
        tvBackupRestore = findViewById(R.id.tv_backup_restore);
        tvLogout = findViewById(R.id.tv_logout);
    }

    // --- 2. Xử lý Giao diện (Dark Mode & Ngôn ngữ) ---
    private void setupDisplaySection() {
        // Load and setup Dark Mode
        if (switchDarkMode != null) {
            boolean isDarkMode = settingsPrefs.getBoolean("dark_mode", false);
            // Set checked before listener to avoid triggering during initialization
            switchDarkMode.setOnCheckedChangeListener(null);
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

        // Language selection removed per request
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
            tvSecurity.setOnClickListener(v -> showPinDialog());
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

    // Language selection removed per request

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

    private void showPinDialog() {
        try {
            String savedPin = settingsPrefs.getString("app_pin", "");
            boolean hasPin = !savedPin.isEmpty();

            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
            View dialogView = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.dialog_pin, null);
            builder.setView(dialogView);
            builder.setCancelable(true);

            AlertDialog dialog = builder.create();

            TextView tvTitle = dialogView.findViewById(R.id.tv_pin_title);
            TextView tvSubtitle = dialogView.findViewById(R.id.tv_pin_subtitle);
            TextView tvError = dialogView.findViewById(R.id.tv_pin_error);
            EditText edtPinHidden = dialogView.findViewById(R.id.edt_pin_hidden);
            View dot1 = dialogView.findViewById(R.id.dot1);
            View dot2 = dialogView.findViewById(R.id.dot2);
            View dot3 = dialogView.findViewById(R.id.dot3);
            View dot4 = dialogView.findViewById(R.id.dot4);

            View[] dots = {dot1, dot2, dot3, dot4};

            if (hasPin) {
                tvTitle.setText("Nhập mã PIN");
                tvSubtitle.setText("Nhập mã PIN để truy cập");
            } else {
                tvTitle.setText("Thiết lập mã PIN");
                tvSubtitle.setText("Nhập 4 chữ số để bảo mật");
            }

            for (int i = 0; i <= 9; i++) {
                int buttonId = getResources().getIdentifier("btn_pin_" + i, "id", getPackageName());
                Button btn = dialogView.findViewById(buttonId);
                if (btn != null) {
                    final String digit = String.valueOf(i);
                    btn.setOnClickListener(v -> {
                        String currentPin = edtPinHidden.getText().toString();
                        if (currentPin.length() < 4) {
                            edtPinHidden.setText(currentPin + digit);
                            updatePinDots(dots, edtPinHidden.getText().toString());
                            tvError.setVisibility(View.GONE);

                            if (edtPinHidden.getText().toString().length() == 4) {
                                handlePinInput(edtPinHidden.getText().toString(), hasPin, savedPin, dialog, tvError, dots, edtPinHidden);
                            }
                        }
                    });
                }
            }

            Button btnClear = dialogView.findViewById(R.id.btn_pin_clear);
            if (btnClear != null) {
                btnClear.setOnClickListener(v -> {
                    edtPinHidden.setText("");
                    updatePinDots(dots, "");
                    tvError.setVisibility(View.GONE);
                });
            }

            Button btnBackspace = dialogView.findViewById(R.id.btn_pin_backspace);
            if (btnBackspace != null) {
                btnBackspace.setOnClickListener(v -> {
                    String currentPin = edtPinHidden.getText().toString();
                    if (currentPin.length() > 0) {
                        edtPinHidden.setText(currentPin.substring(0, currentPin.length() - 1));
                        updatePinDots(dots, edtPinHidden.getText().toString());
                        tvError.setVisibility(View.GONE);
                    }
                });
            }

            builder.setNegativeButton("Hủy", null);
            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing PIN dialog: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(SettingsActivity.this, "Lỗi khi mở PIN dialog", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePinDots(View[] dots, String pin) {
        for (int i = 0; i < dots.length; i++) {
            if (i < pin.length()) {
                dots[i].setBackgroundResource(R.drawable.pin_dot_filled);
            } else {
                dots[i].setBackgroundResource(R.drawable.pin_dot_empty);
            }
        }
    }

    private void handlePinInput(String pin, boolean hasPin, String savedPin, AlertDialog dialog, TextView tvError, View[] dots, EditText edtPinHidden) {
        if (hasPin) {
            if (pin.equals(savedPin)) {
                showPinOptionsDialog();
                dialog.dismiss();
            } else {
                tvError.setText("Mã PIN không đúng!");
                tvError.setVisibility(View.VISIBLE);
                edtPinHidden.setText("");
                updatePinDots(dots, "");
            }
        } else {
            dialog.dismiss();
            showPinConfirmationDialog(pin);
        }
    }

    private void showPinConfirmationDialog(String firstPin) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
            View dialogView = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.dialog_pin, null);
            builder.setView(dialogView);
            builder.setCancelable(true);

            AlertDialog dialog = builder.create();

            TextView tvTitle = dialogView.findViewById(R.id.tv_pin_title);
            TextView tvSubtitle = dialogView.findViewById(R.id.tv_pin_subtitle);
            TextView tvError = dialogView.findViewById(R.id.tv_pin_error);
            EditText edtPinHidden = dialogView.findViewById(R.id.edt_pin_hidden);
            View dot1 = dialogView.findViewById(R.id.dot1);
            View dot2 = dialogView.findViewById(R.id.dot2);
            View dot3 = dialogView.findViewById(R.id.dot3);
            View dot4 = dialogView.findViewById(R.id.dot4);

            View[] dots = {dot1, dot2, dot3, dot4};

            tvTitle.setText("Xác nhận mã PIN");
            tvSubtitle.setText("Nhập lại mã PIN để xác nhận");

            for (int i = 0; i <= 9; i++) {
                int buttonId = getResources().getIdentifier("btn_pin_" + i, "id", getPackageName());
                Button btn = dialogView.findViewById(buttonId);
                if (btn != null) {
                    final String digit = String.valueOf(i);
                    btn.setOnClickListener(v -> {
                        String currentPin = edtPinHidden.getText().toString();
                        if (currentPin.length() < 4) {
                            edtPinHidden.setText(currentPin + digit);
                            updatePinDots(dots, edtPinHidden.getText().toString());
                            tvError.setVisibility(View.GONE);

                            if (edtPinHidden.getText().toString().length() == 4) {
                                String confirmPin = edtPinHidden.getText().toString();
                                if (confirmPin.equals(firstPin)) {
                                    settingsPrefs.edit().putString("app_pin", confirmPin).apply();
                                    settingsPrefs.edit().putBoolean("pin_enabled", true).apply();
                                    dialog.dismiss();
                                    Toast.makeText(SettingsActivity.this, "Đã thiết lập mã PIN thành công", Toast.LENGTH_SHORT).show();
                                } else {
                                    tvError.setText("Mã PIN không khớp! Vui lòng thử lại.");
                                    tvError.setVisibility(View.VISIBLE);
                                    edtPinHidden.setText("");
                                    updatePinDots(dots, "");
                                }
                            }
                        }
                    });
                }
            }

            Button btnClear = dialogView.findViewById(R.id.btn_pin_clear);
            if (btnClear != null) {
                btnClear.setOnClickListener(v -> {
                    edtPinHidden.setText("");
                    updatePinDots(dots, "");
                    tvError.setVisibility(View.GONE);
                });
            }

            Button btnBackspace = dialogView.findViewById(R.id.btn_pin_backspace);
            if (btnBackspace != null) {
                btnBackspace.setOnClickListener(v -> {
                    String currentPin = edtPinHidden.getText().toString();
                    if (currentPin.length() > 0) {
                        edtPinHidden.setText(currentPin.substring(0, currentPin.length() - 1));
                        updatePinDots(dots, edtPinHidden.getText().toString());
                        tvError.setVisibility(View.GONE);
                    }
                });
            }

            builder.setNegativeButton("Hủy", null);
            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing PIN confirmation dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showPinOptionsDialog() {
        try {
            String[] options = {"Thay đổi mã PIN", "Tắt mã PIN"};
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
            builder.setTitle("Tùy chọn mã PIN");
            builder.setItems(options, (dialog, which) -> {
                if (which == 0) {
                    dialog.dismiss();
                    showChangePinDialog();
                } else {
                    showDisablePinDialog();
                }
            });
            builder.setNegativeButton("Hủy", null);
            builder.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing PIN options dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showChangePinDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
            View dialogView = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.dialog_pin, null);
            builder.setView(dialogView);
            builder.setCancelable(true);

            AlertDialog dialog = builder.create();

            TextView tvTitle = dialogView.findViewById(R.id.tv_pin_title);
            TextView tvSubtitle = dialogView.findViewById(R.id.tv_pin_subtitle);
            TextView tvError = dialogView.findViewById(R.id.tv_pin_error);
            EditText edtPinHidden = dialogView.findViewById(R.id.edt_pin_hidden);
            View dot1 = dialogView.findViewById(R.id.dot1);
            View dot2 = dialogView.findViewById(R.id.dot2);
            View dot3 = dialogView.findViewById(R.id.dot3);
            View dot4 = dialogView.findViewById(R.id.dot4);

            View[] dots = {dot1, dot2, dot3, dot4};

            String savedPin = settingsPrefs.getString("app_pin", "");

            tvTitle.setText("Nhập mã PIN cũ");
            tvSubtitle.setText("Nhập mã PIN hiện tại để tiếp tục");

            for (int i = 0; i <= 9; i++) {
                int buttonId = getResources().getIdentifier("btn_pin_" + i, "id", getPackageName());
                Button btn = dialogView.findViewById(buttonId);
                if (btn != null) {
                    final String digit = String.valueOf(i);
                    btn.setOnClickListener(v -> {
                        String currentPin = edtPinHidden.getText().toString();
                        if (currentPin.length() < 4) {
                            edtPinHidden.setText(currentPin + digit);
                            updatePinDots(dots, edtPinHidden.getText().toString());
                            tvError.setVisibility(View.GONE);

                            if (edtPinHidden.getText().toString().length() == 4) {
                                String enteredPin = edtPinHidden.getText().toString();
                                if (enteredPin.equals(savedPin)) {
                                    dialog.dismiss();
                                    showSetNewPinDialog();
                                } else {
                                    tvError.setText("Mã PIN không đúng!");
                                    tvError.setVisibility(View.VISIBLE);
                                    edtPinHidden.setText("");
                                    updatePinDots(dots, "");
                                }
                            }
                        }
                    });
                }
            }

            Button btnClear = dialogView.findViewById(R.id.btn_pin_clear);
            if (btnClear != null) {
                btnClear.setOnClickListener(v -> {
                    edtPinHidden.setText("");
                    updatePinDots(dots, "");
                    tvError.setVisibility(View.GONE);
                });
            }

            Button btnBackspace = dialogView.findViewById(R.id.btn_pin_backspace);
            if (btnBackspace != null) {
                btnBackspace.setOnClickListener(v -> {
                    String currentPin = edtPinHidden.getText().toString();
                    if (currentPin.length() > 0) {
                        edtPinHidden.setText(currentPin.substring(0, currentPin.length() - 1));
                        updatePinDots(dots, edtPinHidden.getText().toString());
                        tvError.setVisibility(View.GONE);
                    }
                });
            }

            builder.setNegativeButton("Hủy", null);
            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing change PIN dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showSetNewPinDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
            View dialogView = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.dialog_pin, null);
            builder.setView(dialogView);
            builder.setCancelable(true);

            AlertDialog dialog = builder.create();

            TextView tvTitle = dialogView.findViewById(R.id.tv_pin_title);
            TextView tvSubtitle = dialogView.findViewById(R.id.tv_pin_subtitle);
            TextView tvError = dialogView.findViewById(R.id.tv_pin_error);
            EditText edtPinHidden = dialogView.findViewById(R.id.edt_pin_hidden);
            View dot1 = dialogView.findViewById(R.id.dot1);
            View dot2 = dialogView.findViewById(R.id.dot2);
            View dot3 = dialogView.findViewById(R.id.dot3);
            View dot4 = dialogView.findViewById(R.id.dot4);

            View[] dots = {dot1, dot2, dot3, dot4};

            tvTitle.setText("Mã PIN mới");
            tvSubtitle.setText("Nhập mã PIN mới (4 chữ số)");

            final String[] firstPin = {""};

            for (int i = 0; i <= 9; i++) {
                int buttonId = getResources().getIdentifier("btn_pin_" + i, "id", getPackageName());
                Button btn = dialogView.findViewById(buttonId);
                if (btn != null) {
                    final String digit = String.valueOf(i);
                    btn.setOnClickListener(v -> {
                        String currentPin = edtPinHidden.getText().toString();
                        if (currentPin.length() < 4) {
                            edtPinHidden.setText(currentPin + digit);
                            updatePinDots(dots, edtPinHidden.getText().toString());
                            tvError.setVisibility(View.GONE);

                            if (edtPinHidden.getText().toString().length() == 4) {
                                if (firstPin[0].isEmpty()) {
                                    firstPin[0] = edtPinHidden.getText().toString();
                                    dialog.dismiss();
                                    showConfirmNewPinDialog(firstPin[0]);
                                }
                            }
                        }
                    });
                }
            }

            Button btnClear = dialogView.findViewById(R.id.btn_pin_clear);
            if (btnClear != null) {
                btnClear.setOnClickListener(v -> {
                    edtPinHidden.setText("");
                    updatePinDots(dots, "");
                    tvError.setVisibility(View.GONE);
                });
            }

            Button btnBackspace = dialogView.findViewById(R.id.btn_pin_backspace);
            if (btnBackspace != null) {
                btnBackspace.setOnClickListener(v -> {
                    String currentPin = edtPinHidden.getText().toString();
                    if (currentPin.length() > 0) {
                        edtPinHidden.setText(currentPin.substring(0, currentPin.length() - 1));
                        updatePinDots(dots, edtPinHidden.getText().toString());
                        tvError.setVisibility(View.GONE);
                    }
                });
            }

            builder.setNegativeButton("Hủy", null);
            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing set new PIN dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showConfirmNewPinDialog(String firstPin) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
            View dialogView = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.dialog_pin, null);
            builder.setView(dialogView);
            builder.setCancelable(true);

            AlertDialog dialog = builder.create();

            TextView tvTitle = dialogView.findViewById(R.id.tv_pin_title);
            TextView tvSubtitle = dialogView.findViewById(R.id.tv_pin_subtitle);
            TextView tvError = dialogView.findViewById(R.id.tv_pin_error);
            EditText edtPinHidden = dialogView.findViewById(R.id.edt_pin_hidden);
            View dot1 = dialogView.findViewById(R.id.dot1);
            View dot2 = dialogView.findViewById(R.id.dot2);
            View dot3 = dialogView.findViewById(R.id.dot3);
            View dot4 = dialogView.findViewById(R.id.dot4);

            View[] dots = {dot1, dot2, dot3, dot4};

            tvTitle.setText("Xác nhận mã PIN mới");
            tvSubtitle.setText("Nhập lại mã PIN mới để xác nhận");

            for (int i = 0; i <= 9; i++) {
                int buttonId = getResources().getIdentifier("btn_pin_" + i, "id", getPackageName());
                Button btn = dialogView.findViewById(buttonId);
                if (btn != null) {
                    final String digit = String.valueOf(i);
                    btn.setOnClickListener(v -> {
                        String currentPin = edtPinHidden.getText().toString();
                        if (currentPin.length() < 4) {
                            edtPinHidden.setText(currentPin + digit);
                            updatePinDots(dots, edtPinHidden.getText().toString());
                            tvError.setVisibility(View.GONE);

                            if (edtPinHidden.getText().toString().length() == 4) {
                                String confirmPin = edtPinHidden.getText().toString();
                                if (confirmPin.equals(firstPin)) {
                                    settingsPrefs.edit().putString("app_pin", confirmPin).apply();
                                    dialog.dismiss();
                                    Toast.makeText(SettingsActivity.this, "Đã thay đổi mã PIN thành công", Toast.LENGTH_SHORT).show();
                                } else {
                                    tvError.setText("Mã PIN không khớp! Vui lòng thử lại.");
                                    tvError.setVisibility(View.VISIBLE);
                                    edtPinHidden.setText("");
                                    updatePinDots(dots, "");
                                }
                            }
                        }
                    });
                }
            }

            Button btnClear = dialogView.findViewById(R.id.btn_pin_clear);
            if (btnClear != null) {
                btnClear.setOnClickListener(v -> {
                    edtPinHidden.setText("");
                    updatePinDots(dots, "");
                    tvError.setVisibility(View.GONE);
                });
            }

            Button btnBackspace = dialogView.findViewById(R.id.btn_pin_backspace);
            if (btnBackspace != null) {
                btnBackspace.setOnClickListener(v -> {
                    String currentPin = edtPinHidden.getText().toString();
                    if (currentPin.length() > 0) {
                        edtPinHidden.setText(currentPin.substring(0, currentPin.length() - 1));
                        updatePinDots(dots, edtPinHidden.getText().toString());
                        tvError.setVisibility(View.GONE);
                    }
                });
            }

            builder.setNegativeButton("Hủy", null);
            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing confirm new PIN dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showDisablePinDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
            builder.setTitle("Tắt mã PIN");
            builder.setMessage("Bạn có chắc chắn muốn tắt mã PIN?");
            builder.setPositiveButton("Đồng ý", (dialog, which) -> {
                settingsPrefs.edit().remove("app_pin").apply();
                settingsPrefs.edit().putBoolean("pin_enabled", false).apply();
                Toast.makeText(SettingsActivity.this, "Đã tắt mã PIN", Toast.LENGTH_SHORT).show();
            });
            builder.setNegativeButton("Hủy", null);
            builder.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing disable PIN dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
