package com.example.campusexpensesmanagermer.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
            // Load Dark Mode setting (set checked before listener to avoid triggering)
            if (switchDarkMode != null) {
                boolean isDarkMode = settingsPrefs.getBoolean("dark_mode", false);
                // Temporarily remove listener to prevent triggering during load
                switchDarkMode.setOnCheckedChangeListener(null);
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
            if (tvCurrency != null) {
                tvCurrency.setText("Tiền tệ: " + currency);
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

        // Security Settings
        if (tvSecurity != null) {
            tvSecurity.setOnClickListener(v -> showPinDialog());
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

    private void showPinDialog() {
        try {
            String savedPin = settingsPrefs.getString("app_pin", "");
            boolean hasPin = !savedPin.isEmpty();

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_pin, null);
            builder.setView(dialogView);
            builder.setCancelable(true);

            AlertDialog dialog = builder.create();

            // Get views
            TextView tvTitle = dialogView.findViewById(R.id.tv_pin_title);
            TextView tvSubtitle = dialogView.findViewById(R.id.tv_pin_subtitle);
            TextView tvError = dialogView.findViewById(R.id.tv_pin_error);
            EditText edtPinHidden = dialogView.findViewById(R.id.edt_pin_hidden);
            View dot1 = dialogView.findViewById(R.id.dot1);
            View dot2 = dialogView.findViewById(R.id.dot2);
            View dot3 = dialogView.findViewById(R.id.dot3);
            View dot4 = dialogView.findViewById(R.id.dot4);

            View[] dots = {dot1, dot2, dot3, dot4};

            // Set title based on whether PIN exists
            if (hasPin) {
                tvTitle.setText("Nhập mã PIN");
                tvSubtitle.setText("Nhập mã PIN để truy cập");
            } else {
                tvTitle.setText("Thiết lập mã PIN");
                tvSubtitle.setText("Nhập 4 chữ số để bảo mật");
            }

            // Setup number buttons
            for (int i = 0; i <= 9; i++) {
                int buttonId = requireContext().getResources().getIdentifier("btn_pin_" + i, "id", requireContext().getPackageName());
                Button btn = dialogView.findViewById(buttonId);
                if (btn != null) {
                    final String digit = String.valueOf(i);
                    btn.setOnClickListener(v -> {
                        String currentPin = edtPinHidden.getText().toString();
                        if (currentPin.length() < 4) {
                            edtPinHidden.setText(currentPin + digit);
                            updatePinDots(dots, edtPinHidden.getText().toString());
                            tvError.setVisibility(View.GONE);

                            // Auto submit when 4 digits entered
                            if (edtPinHidden.getText().toString().length() == 4) {
                                handlePinInput(edtPinHidden.getText().toString(), hasPin, savedPin, dialog, tvError, dots, edtPinHidden);
                            }
                        }
                    });
                }
            }

            // Clear button
            Button btnClear = dialogView.findViewById(R.id.btn_pin_clear);
            if (btnClear != null) {
                btnClear.setOnClickListener(v -> {
                    edtPinHidden.setText("");
                    updatePinDots(dots, "");
                    tvError.setVisibility(View.GONE);
                });
            }

            // Backspace button
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

            // Cancel button
            builder.setNegativeButton("Hủy", null);

            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing PIN dialog: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(requireContext(), "Lỗi khi mở PIN dialog", Toast.LENGTH_SHORT).show();
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
            // Verify PIN
            if (pin.equals(savedPin)) {
                // PIN correct - show options
                showPinOptionsDialog();
                dialog.dismiss();
            } else {
                // PIN incorrect
                tvError.setText("Mã PIN không đúng!");
                tvError.setVisibility(View.VISIBLE);
                edtPinHidden.setText("");
                updatePinDots(dots, "");
            }
        } else {
            // Set new PIN - need confirmation
            dialog.dismiss();
            showPinConfirmationDialog(pin);
        }
    }

    private void showPinConfirmationDialog(String firstPin) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_pin, null);
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

            // Setup number buttons
            for (int i = 0; i <= 9; i++) {
                int buttonId = requireContext().getResources().getIdentifier("btn_pin_" + i, "id", requireContext().getPackageName());
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
                                    // PINs match - save it
                                    settingsPrefs.edit().putString("app_pin", confirmPin).apply();
                                    settingsPrefs.edit().putBoolean("pin_enabled", true).apply();
                                    dialog.dismiss();
                                    Toast.makeText(requireContext(), "Đã thiết lập mã PIN thành công", Toast.LENGTH_SHORT).show();
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
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Tùy chọn mã PIN");
            builder.setItems(options, (dialog, which) -> {
                if (which == 0) {
                    // Change PIN
                    dialog.dismiss();
                    showChangePinDialog();
                } else {
                    // Disable PIN
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
        // Show dialog to enter old PIN first
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_pin, null);
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

            // Setup number buttons
            for (int i = 0; i <= 9; i++) {
                int buttonId = requireContext().getResources().getIdentifier("btn_pin_" + i, "id", requireContext().getPackageName());
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
                                    // Old PIN correct - proceed to set new PIN
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
        // Similar to showPinConfirmationDialog but for changing PIN
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_pin, null);
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

            // Setup number buttons
            for (int i = 0; i <= 9; i++) {
                int buttonId = requireContext().getResources().getIdentifier("btn_pin_" + i, "id", requireContext().getPackageName());
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
                                    // First PIN entered
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
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_pin, null);
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

            // Setup number buttons
            for (int i = 0; i <= 9; i++) {
                int buttonId = requireContext().getResources().getIdentifier("btn_pin_" + i, "id", requireContext().getPackageName());
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
                                    // PINs match - save it
                                    settingsPrefs.edit().putString("app_pin", confirmPin).apply();
                                    dialog.dismiss();
                                    Toast.makeText(requireContext(), "Đã thay đổi mã PIN thành công", Toast.LENGTH_SHORT).show();
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
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Tắt mã PIN");
            builder.setMessage("Bạn có chắc chắn muốn tắt mã PIN?");
            builder.setPositiveButton("Đồng ý", (dialog, which) -> {
                settingsPrefs.edit().remove("app_pin").apply();
                settingsPrefs.edit().putBoolean("pin_enabled", false).apply();
                Toast.makeText(requireContext(), "Đã tắt mã PIN", Toast.LENGTH_SHORT).show();
            });
            builder.setNegativeButton("Hủy", null);
            builder.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing disable PIN dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
