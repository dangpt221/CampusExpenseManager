package com.example.campusexpensesmanagermer.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.campusexpensesmanagermer.R;
// ⚠️ QUAN TRỌNG: Kiểm tra lại dòng import LoginActivity này
import com.example.campusexpensesmanagermer.Activities.LoginActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.File;

public class SettingFragment extends Fragment {

    private SharedPreferences sharedPreferences;

    // ⚠️ LƯU Ý: Sửa tên database cho giống trong SQLiteDbHelper của bạn
    private static final String DB_NAME = "campus_expense";

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null) {
            sharedPreferences = getActivity().getSharedPreferences("UserSettings", Context.MODE_PRIVATE);
        }


        Button btnBack = view.findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                // Kiểm tra xem Activity chứa Fragment này có phải là HomeActivity không
                if (getActivity() instanceof com.example.campusexpensesmanagermer.Activities.HomeActivity) {
                    // Gọi hàm chuyển Tab mà chúng ta vừa viết ở Bước 1
                    ((com.example.campusexpensesmanagermer.Activities.HomeActivity) getActivity()).goToHome();
                }
            });
        }

        // ======================= 1. DARK MODE =======================
        SwitchMaterial switchDarkMode = view.findViewById(R.id.switchDarkMode);
        if (switchDarkMode != null) {
            boolean isDarkMode = sharedPreferences.getBoolean("DARK_MODE", false);
            switchDarkMode.setChecked(isDarkMode);

            switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
                sharedPreferences.edit().putBoolean("DARK_MODE", isChecked).apply();
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            });
        }

        // ======================= 2. NGÔN NGỮ =======================
        LinearLayout layoutLanguage = view.findViewById(R.id.layoutLanguage);
        TextView tvCurrentLang = view.findViewById(R.id.tvCurrentLang);
        if (layoutLanguage != null) {
            layoutLanguage.setOnClickListener(v -> showLanguageDialog(tvCurrentLang));
        }

        // ======================= 3. ĐẶT MÃ PIN =======================
        LinearLayout layoutSecurity = view.findViewById(R.id.layoutSecurity);
        if (layoutSecurity != null) {
            layoutSecurity.setOnClickListener(v -> showSetPinDialog());
        }

        // ======================= 4. SAO LƯU DỮ LIỆU =======================
        LinearLayout layoutBackup = view.findViewById(R.id.layoutBackup);
        if (layoutBackup != null) {
            layoutBackup.setOnClickListener(v -> {
                if (checkStoragePermission()) {
                    backupDatabase();
                } else {
                    requestStoragePermission();
                }
            });
        }

        // ======================= 5. ĐĂNG XUẤT =======================
        Button btnLogout = view.findViewById(R.id.btnLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> showLogoutConfirmation());
        }
    }

    // --- HÀM HỖ TRỢ ĐĂNG XUẤT ---
    private void showLogoutConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Log out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> logoutUser())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void logoutUser() {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        }

        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    // --- HÀM HỖ TRỢ NGÔN NGỮ ---
    private void showLanguageDialog(TextView tvDisplay) {
        final String[] languages = {"Vietnamese", "English", "Japanese"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Select language")
                .setItems(languages, (dialog, which) -> {
                    if (tvDisplay != null) tvDisplay.setText(languages[which]);
                    Toast.makeText(getContext(), "Selected: " + languages[which], Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    // --- HÀM HỖ TRỢ ĐẶT MÃ PIN ---
    private void showSetPinDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Set PIN");
        builder.setMessage("Enter a 4-digit PIN:");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String pin = input.getText().toString();
            if (pin.length() == 4) {
                sharedPreferences.edit().putString("USER_PIN", pin).apply();
                sharedPreferences.edit().putBoolean("IS_PIN_ENABLED", true).apply();
                Toast.makeText(getContext(), "PIN set successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Please enter exactly 4 digits!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    // --- HÀM HỖ TRỢ SAO LƯU ---
    private void backupDatabase() {
        try {
            File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + requireContext().getPackageName() + "//databases//" + DB_NAME ;
                String backupDBPath = "Backup_Expense_" + System.currentTimeMillis() + ".db";

                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    try (java.io.FileInputStream fis = new java.io.FileInputStream(currentDB);
                         java.io.FileOutputStream fos = new java.io.FileOutputStream(backupDB)) {
                        fos.getChannel().transferFrom(fis.getChannel(), 0, fis.getChannel().size());
                        Toast.makeText(getContext(), "Backup saved to Downloads!", Toast.LENGTH_LONG).show();
                    } catch (Exception ex) {
                        Toast.makeText(getContext(), "Error during backup: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Original database not found (" + DB_NAME + ")!", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // --- CẤP QUYỀN ---
    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) return true;
        int write = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        return write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }
}